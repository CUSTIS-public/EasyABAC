package custis.easyabac.core;

import custis.easyabac.core.audit.Audit;
import custis.easyabac.core.extend.RequestExtender;
import custis.easyabac.core.pdp.*;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.core.trace.model.TraceResult;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.IdGenerator;
import custis.easyabac.model.attribute.Attribute;
import custis.easyabac.model.attribute.AttributeWithValue;
import custis.easyabac.model.attribute.Category;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.stream.Collectors;

public class EasyAbac implements AttributiveAuthorizationService {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PdpHandler pdpHandler;
    private final AbacAuthModel abacAuthModel;
    private final List<RequestExtender> requestExtenders;
    private final Audit audit;
    private final Trace trace;
    private final Map<String, Map<String, Attribute>> attributesByAction;
    private final Options options;

    EasyAbac(PdpHandler pdpHandler, AbacAuthModel abacAuthModel, List<RequestExtender> requestExtenders,
             Audit audit, Trace trace, Map<String, Map<String, Attribute>> attributesByAction, Options options) {

        this.pdpHandler = pdpHandler;
        this.abacAuthModel = abacAuthModel;
        this.requestExtenders = requestExtenders;
        this.audit = audit;
        this.trace = trace;
        this.attributesByAction = attributesByAction;
        this.options = options;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> authAttributes) {
        Map<String, Attribute> allAttributes = new HashMap<>();
        try {
            // TODO: 17.10.18 сделать что то с allAttributes
            List<AttributeWithValue> attributeWithValueList = enrichAttributes(authAttributes, allAttributes);
            for (RequestExtender extender : requestExtenders) {
                extender.extend(attributeWithValueList);
            }

            AuthResponse result = pdpHandler.evaluate(attributeWithValueList);


            if (options.isEnableTrace()) {
                TraceResult traceResult = result.getTraceResult();
                if (traceResult != null) {
                    if (!pdpHandler.xacmlPolicyMode()) {
                        traceResult.populateByModel(abacAuthModel);
                    }
                }
                trace.handleTrace(abacAuthModel, traceResult);
            }

            if (options.isEnableAudit()) {
                performAudit(attributeWithValueList, result);
            }

            return result;
        } catch (Exception e) {
            log.error("authorize", e);
            return new AuthResponse(e.getMessage());
        }
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {

        final boolean enableOptimization = isEnableOptimization(attributes.size());

        List<AttributeWithValue> additionalAttributes = new ArrayList<>();

        for (RequestExtender extender : requestExtenders) {
            extender.extend(additionalAttributes);
        }

        MultiAuthRequest multiAuthRequest = null;
        MultiAuthResponse result = null;
        Map<RequestId, List<RequestId>> requestIdOptimizedMap = null;

        if (enableOptimization) {
            MultiAuthRequestOptimize multiAuthRequestOptimize = optimazePrepareMultiRequest(attributes, additionalAttributes);
            requestIdOptimizedMap = new HashMap<>();

            Map<RequestId, List<String>> requests = multiAuthRequestOptimize.getRequests();
            Map<RequestId, List<String>> uniqueRequests = requests.values().stream().distinct().collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

            for (RequestId uniqueRequestId : uniqueRequests.keySet()) {
                List<RequestId> requestIdsByOptimRequest = requests.keySet().stream()
                        .filter(requestId -> requests.get(requestId).equals(uniqueRequests.get(uniqueRequestId)))
                        .collect(Collectors.toList());
                requestIdOptimizedMap.put(uniqueRequestId, requestIdsByOptimRequest);
            }

            MultiAuthRequestOptimize uniqueMultiAuthRequest = new MultiAuthRequestOptimize(multiAuthRequestOptimize.getAttributesWithValue(), uniqueRequests);

            try {
                result = pdpHandler.evaluate(uniqueMultiAuthRequest);
            } catch (EasyAbacInitException e) {
                log.error("authorizeMultiple ", e);
            }
        } else {
            multiAuthRequest = prepareMultiRequest(attributes, additionalAttributes);
            try {
                result = pdpHandler.evaluate(multiAuthRequest);
            } catch (EasyAbacInitException e) {
                log.error("authorizeMultiple ", e);
            }
        }


        if (enableOptimization) {
            Map<RequestId, AuthResponse> detailedResult = new HashMap<>();
            for (RequestId optimRequestId : result.getResults().keySet()) {
                List<RequestId> requestIds = requestIdOptimizedMap.get(optimRequestId);
                AuthResponse authResponse = result.getResults().get(optimRequestId);
                requestIds.forEach(requestId -> detailedResult.put(requestId, authResponse));
            }
            result = new MultiAuthResponse(detailedResult);
        }


        if (options.isEnableTrace()) {
            result.getResults().forEach((requestId, authResponse) -> {
                TraceResult traceResult = authResponse.getTraceResult();
                if (traceResult != null) {
                    if (!pdpHandler.xacmlPolicyMode()) {
                        traceResult.populateByModel(abacAuthModel);
                    }
                    trace.handleTrace(abacAuthModel, traceResult);
                }
            });
        }


        if (options.isEnableAudit()) {
            performAudit(multiAuthRequest, result);
        }

        return result.getResults();
    }

    private boolean isEnableOptimization(int numberOfRequests) {
        return options.isEnableOptimization() && options.getOptimizationThreshold() < numberOfRequests;
    }

    private MultiAuthRequest prepareMultiRequest(Map<RequestId, List<AuthAttribute>> authAttributes, List<AttributeWithValue> additionalAttributes) {

        Map<String, Attribute> allAttributesById = new HashMap<>();

        Map<String, Attribute> additionalAttributesMap = additionalAttributes.stream().map(attributeWithValue -> attributeWithValue.getAttribute())
                .collect(Collectors.toMap(Attribute::getId, a -> a, (oldValue, newValue) -> oldValue));
        allAttributesById.putAll(additionalAttributesMap);


        Map<RequestId, List<AttributeWithValue>> requests = new HashMap<>();

        for (RequestId requestId : authAttributes.keySet()) {

            List<AuthAttribute> authAttributesByRequest = authAttributes.get(requestId);
            List<AttributeWithValue> attributeWithValuesByRequest = enrichAttributes(authAttributesByRequest, allAttributesById);

            attributeWithValuesByRequest.addAll(additionalAttributes);

            requests.put(requestId, attributeWithValuesByRequest);
        }

        return new MultiAuthRequest(allAttributesById, requests);
    }

    private MultiAuthRequestOptimize optimazePrepareMultiRequest(Map<RequestId, List<AuthAttribute>> authAttributes, List<AttributeWithValue> additionalAttributes) {

        List<AuthAttribute> authAttributeList = authAttributes.values().stream().flatMap(a -> a.stream()).collect(Collectors.toList());

        List<AuthAttribute> additionalAuthAttributes = additionalAttributes.stream().map(a -> new AuthAttribute(a.getAttribute().getId(), a.getValues())).collect(Collectors.toList());

        authAttributeList.addAll(additionalAuthAttributes);

        Map<AuthAttribute, String> uniqueAttributesWithValue = authAttributeList.stream()
                .distinct()
                .collect(Collectors.toMap(o -> o, o -> IdGenerator.newId()));

        Map<String, Attribute> modelAttributes = abacAuthModel.getAttributes();

        Map<RequestId, List<String>> clearedRequests = new HashMap<>();

        for (RequestId requestId : authAttributes.keySet()) {

            List<AuthAttribute> requestAuthAttributes = authAttributes.get(requestId);
            requestAuthAttributes.addAll(additionalAuthAttributes);

            List<Attribute> clearedAttributeList = requestAuthAttributes.stream().map(a -> modelAttributes.get(a.getId()))
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            Attribute actionAttributeFromRequest = getActionAttributeFromRequest(clearedAttributeList);

            String actionFromRequest = requestAuthAttributes.stream().filter(a -> a.getId().equals(actionAttributeFromRequest.getId())).findFirst().get().getValues().get(0);

            clearedAttributeList = clearedAttributeList.stream().filter(a -> attributesByAction.get(actionFromRequest).get(a.getId()) != null).collect(Collectors.toList());

            List<String> attributeWithValueIdList = clearedAttributeList.stream()
                    .flatMap(a -> requestAuthAttributes.stream()
                            .filter(authAttribute -> authAttribute.getId().equals(a.getId())))
                    .map(a -> uniqueAttributesWithValue.get(a))
                    .sorted()
                    .collect(Collectors.toList());


            clearedRequests.put(requestId, attributeWithValueIdList);
        }
        Map<String, AuthAttribute> uniqueAttributesWithValueById = uniqueAttributesWithValue.keySet().stream().collect(Collectors.toMap(o -> uniqueAttributesWithValue.get(o), o -> o));


        Map<String, AuthAttribute> clearedAttributesWithValueById = clearedRequests.values().stream()
                .flatMap(o -> o.stream())
                .distinct()
                .collect(Collectors.toMap(o -> o, o -> uniqueAttributesWithValueById.get(o)));
        return new MultiAuthRequestOptimize(clearedAttributesWithValueById, clearedRequests);
    }

    private List<AttributeWithValue> enrichAttributes(List<AuthAttribute> authAttributesByRequest, Map<String, Attribute> allAttributes) {

        List<AttributeWithValue> attributeWithValues = new ArrayList<>();
        for (AuthAttribute authAttribute : authAttributesByRequest) {

            Attribute attribute = allAttributes.get(authAttribute.getId());
            if (attribute == null) {
                attribute = abacAuthModel.getAttributes().get(authAttribute.getId());
                if (attribute == null) {
                    log.warn("Attribute " + authAttribute.getId() + " is not found in the model");
                    continue;
                }
                allAttributes.put(attribute.getId(), attribute);
            }

            AttributeWithValue attributeWithValue = new AttributeWithValue(attribute, authAttribute.getValues());
            attributeWithValues.add(attributeWithValue);
        }
        return attributeWithValues;
    }

    private Attribute getActionAttributeFromRequest(List<Attribute> attributesByRequest) throws EasyAbacAuthException {
        List<Attribute> actions = attributesByRequest.stream().filter(attribute -> attribute.getCategory().equals(Category.ACTION)).collect(Collectors.toList());
        if (actions.size() != 1) {
            throw new EasyAbacAuthException("The request must have only one action");
        }

        return actions.get(0);
    }


    private String getActionFromRequest(List<AttributeWithValue> authAttributesByRequest) throws EasyAbacAuthException {
        List<AttributeWithValue> actions = authAttributesByRequest.stream().filter(authAttribute -> authAttribute.getAttribute().getCategory().equals(Category.ACTION)).collect(Collectors.toList());
        if (actions.size() != 1) {
            throw new EasyAbacAuthException("The request must have only one action");
        }

        return actions.get(0).getValues().get(0);
    }

    private void performAudit(List<AttributeWithValue> attributeWithValues, AuthResponse result) {
        List<AttributeWithValue> subject = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.SUBJECT)
                .collect(Collectors.toList());

        Optional<AttributeWithValue> action = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.ACTION)
                .findFirst();

        Map<String, String> resourceMap = new HashMap<>();
        attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.RESOURCE)
                .forEach(attributeWithValue -> resourceMap.put(attributeWithValue.getAttribute().getId(), attributeWithValue.getValues().toString()));


        audit.onAction(serializeSubject(subject), resourceMap, action.get().getValues().get(0), result.getDecision());
    }

    private void performAudit(MultiAuthRequest requestContext, MultiAuthResponse response) {
        List<AttributeWithValue> subject = requestContext.getRequests().values()
                .stream()
                .flatMap(attributeWithValueList -> attributeWithValueList.stream())
                .filter(attribute -> attribute.getAttribute().getCategory() == Category.SUBJECT)
                .collect(Collectors.toList());

        List<AttributeWithValue> actions = requestContext.getRequests().values()
                .stream()
                .flatMap(attributeWithValueList -> attributeWithValueList.stream())
                .filter(attribute -> attribute.getAttribute().getCategory() == Category.ACTION)
                .collect(Collectors.toList());
        // FIXME сделать

        audit.onMultipleActions(serializeSubject(subject), Collections.emptyMap(), Collections.emptyMap());
    }

    private static String serializeSubject(List<AttributeWithValue> subject) {
        return subject.toString();
    }

}