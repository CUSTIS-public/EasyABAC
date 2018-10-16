package custis.easyabac.core;

import custis.easyabac.core.audit.Audit;
import custis.easyabac.core.audit.DefaultAudit;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.extend.RequestExtender;
import custis.easyabac.core.extend.subject.DummySubjectAttributesProvider;
import custis.easyabac.core.extend.subject.SubjectAttributesExtender;
import custis.easyabac.core.extend.subject.SubjectAttributesProvider;
import custis.easyabac.core.init.*;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeGroup;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.trace.DefaultTrace;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.core.trace.model.TraceResult;
import custis.easyabac.pdp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EasyAbac implements AttributiveAuthorizationService {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PdpHandler pdpHandler;
    private final AbacAuthModel abacAuthModel;
    private final List<Datasource> datasources;
    private final List<RequestExtender> requestExtenders;
    private final Audit audit;
    private final Trace trace;

    private EasyAbac(PdpHandler pdpHandler, AbacAuthModel abacAuthModel, List<Datasource> datasources, List<RequestExtender> requestExtenders, Audit audit, Trace trace) {
        this.pdpHandler = pdpHandler;
        this.abacAuthModel = abacAuthModel;
        this.datasources = datasources;
        this.requestExtenders = requestExtenders;
        this.audit = audit;
        this.trace = trace;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> authAttributes) {
        Map<String, Attribute> allAttributes = new HashMap<>();
        try {
            List<AttributeWithValue> attributeWithValueList = enrichAttributes(authAttributes, allAttributes);
            for (RequestExtender extender : requestExtenders) {
                extender.extend(attributeWithValueList);
            }

            AuthResponse result = pdpHandler.evaluate(attributeWithValueList);

            TraceResult traceResult = result.getTraceResult();
            if (!pdpHandler.xacmlPolicyMode()) {
                traceResult.populateByModel(abacAuthModel);
            }
            trace.handleTrace(abacAuthModel, traceResult);
            audit.onRequest(attributeWithValueList, result);

            return result;
        } catch (Exception e) {
            log.error("authorize", e);
            return new AuthResponse(e.getMessage());
        }
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {

        MultiAuthRequest multiAuthRequest = prepareMultiRequest(attributes);
//        for (RequestExtender extender : requestExtenders) {
//            extender.extend(requestContext);
//        }

        MultiAuthResponse result = null;
        try {
            result = pdpHandler.evaluate(multiAuthRequest);
        } catch (EasyAbacInitException e) {
            log.error("authorizeMultiple ", e);
        }

        result.getResults().forEach((requestId, authResponse) -> {
            TraceResult traceResult = authResponse.getTraceResult();
            if (!pdpHandler.xacmlPolicyMode()) {
                traceResult.populateByModel(abacAuthModel);
            }
            trace.handleTrace(abacAuthModel, traceResult);
        });
//TODO починить
//        audit.onMultipleRequest(multiAuthRequest, result);

        return result.getResults();
    }

    /**
     * Generating optimizable request
     *
     * @param attributes
     * @return
     */
    private MdpAuthRequest generate(Map<RequestId, List<AuthAttribute>> attributes) {
        // 0. simple request prepare
        MdpAuthRequest request = prepareSimpleMdpAuthRequest(attributes);

        // 1. clearing not used attributes in policies


        return request;
    }

    private MultiAuthRequest prepareMultiRequest(Map<RequestId, List<AuthAttribute>> authAttributes) {

        Map<String, Attribute> allAttributes = new HashMap<>();
        Map<RequestId, List<AttributeWithValue>> requests = new HashMap<>();

        for (RequestId requestId : authAttributes.keySet()) {
            List<AuthAttribute> authAttributesByRequest = authAttributes.get(requestId);

            List<AttributeWithValue> attributeWithValuesByRequest = enrichAttributes(authAttributesByRequest, allAttributes);

            requests.put(requestId, attributeWithValuesByRequest);

        }
        return new MultiAuthRequest(allAttributes, requests);
    }

    private List<AttributeWithValue> enrichAttributes(List<AuthAttribute> authAttributesByRequest, Map<String, Attribute> allAttributes) {
        List<AttributeWithValue> attributeWithValues = new ArrayList<>();
        for (AuthAttribute authAttribute : authAttributesByRequest) {

            Attribute attribute = allAttributes.get(authAttribute.getId());
            if (attribute == null) {
                try {
                    attribute = findAttribute(abacAuthModel.getAttributes(), authAttribute.getId());
                } catch (EasyAbacInitException e) {
                    log.warn(e.getMessage(), e);
                    continue;
                }
                allAttributes.put(attribute.getId(), attribute);
            }

            AttributeWithValue attributeWithValue = new AttributeWithValue(attribute, authAttribute.getValues());
            attributeWithValues.add(attributeWithValue);
        }
        return attributeWithValues;
    }


    private MdpAuthRequest prepareSimpleMdpAuthRequest(Map<RequestId, List<AuthAttribute>> attributes) {
        MdpAuthRequest request = new MdpAuthRequest();

        attributes.forEach((requestId, authAttributes) -> {

            MdpAuthRequest.RequestReference reference = new MdpAuthRequest.RequestReference();
            Map<Category, AttributeGroup> groupMap = new HashMap<>();

            for (AuthAttribute authAttribute : authAttributes) {
                Attribute attribute = abacAuthModel.getAttributes().get(authAttribute.getId());

                AttributeGroup group = groupMap.computeIfAbsent(attribute.getCategory(), category -> new AttributeGroup(requestId + "#" + category, category, new ArrayList<>()));
                group.addAttribute(new AttributeWithValue(attribute, authAttribute.getValues()));
            }

            request.addRequest(reference);
        });

        return request;
    }

    public static Attribute findAttribute(Map<String, Attribute> attributeMap, String attributeId) throws EasyAbacInitException {
        Attribute attributeParam = attributeMap.get(attributeId);
        if (attributeParam == null) {
            throw new EasyAbacInitException("Attribute " + attributeId + " is not found in the model");
        }
        return attributeParam;
    }

    public static class Builder {

        private final AbacAuthModel abacAuthModel;

        private PdpHandlerFactory pdpHandlerFactory = BalanaPdpHandlerFactory.PROXY_INSTANCE;
        private List<Datasource> datasources = Collections.emptyList();
        private Cache cache;
        private Trace trace = DefaultTrace.INSTANCE;
        private Audit audit = DefaultAudit.INSTANCE;
        private SubjectAttributesProvider subjectAttributesProvider = DummySubjectAttributesProvider.INSTANCE;
        private InputStream xacmlPolicy;

        public Builder(AbacAuthModel abacAuthModel) {
            this.abacAuthModel = abacAuthModel;
        }

        public Builder(String easyModel, ModelType modelType) throws EasyAbacInitException {
            this(new ByteArrayInputStream(easyModel.getBytes()), modelType);
        }

        public Builder(InputStream easyModel, ModelType modelType) throws EasyAbacInitException {
            this.abacAuthModel = AbacAuthModelFactory.getInstance(modelType, easyModel);
        }

        public Builder pdpHandlerFactory(PdpHandlerFactory pdpHandlerFactory) {
            if (xacmlPolicy != null && !pdpHandlerFactory.supportsXacmlPolicies()) {
                throw new IllegalArgumentException(pdpHandlerFactory.getClass().getName() + " should supports XACML!");
            }
            this.pdpHandlerFactory = pdpHandlerFactory;
            return this;
        }

        public Builder datasources(List<Datasource> datasources) {
            this.datasources = datasources;
            return this;
        }

        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder trace(Trace trace) {
            this.trace = trace;
            return this;
        }

        public Builder audit(Audit audit) {
            this.audit = audit;
            return this;
        }

        public Builder subjectAttributesProvider(SubjectAttributesProvider subjectAttributesProvider) {
            this.subjectAttributesProvider = subjectAttributesProvider;
            return this;
        }

        public Builder useXacmlPolicy(InputStream xacmlPolicy) {
            if (!pdpHandlerFactory.supportsXacmlPolicies()) {
                throw new IllegalArgumentException(pdpHandlerFactory.getClass().getName() + " doesn't supports XACML!");
            }
            this.xacmlPolicy = xacmlPolicy;
            return this;
        }


        public AttributiveAuthorizationService build() throws EasyAbacInitException {
            enrichDatasources(datasources, abacAuthModel);

            groupAttributesByAction(datasources, abacAuthModel);

            PdpHandler pdpHandler = null;
            if (xacmlPolicy != null) {
                // this is xacml source
                pdpHandler = pdpHandlerFactory.newXacmlInstance(xacmlPolicy, datasources, cache);
            } else {
                pdpHandler = pdpHandlerFactory.newInstance(abacAuthModel, datasources, cache);
            }

            List<RequestExtender> extenders = new ArrayList<>();
            extenders.add(new SubjectAttributesExtender(subjectAttributesProvider));


            if (log.isDebugEnabled()) {
                for (Attribute attribute : abacAuthModel.getAttributes().values()) {
                    log.debug(attribute.getId() + "  ->  " + attribute.getXacmlName() + "  ->  " + attribute.getType().getXacmlName() + "  ->  " + attribute.getCategory().getXacmlName());
                }

            }

            return new EasyAbac(pdpHandler, abacAuthModel, datasources, extenders, audit, trace);
        }

        private void groupAttributesByAction(List<Datasource> datasources, AbacAuthModel abacAuthModel) {
            Set<String> actions = abacAuthModel.getPolicies().stream().flatMap(policy -> policy.getTarget().getAccessToActions().stream()).collect(Collectors.toSet());

            List<Attribute> attributes = abacAuthModel.getPolicies().stream().flatMap(policy -> policy.getRules().stream()
                    .flatMap(rule -> rule.getConditions().stream()
                            .flatMap(condition -> Arrays.asList(condition.getFirstOperand()).stream())))
                    .collect(Collectors.toList());

            Map<String, Map<String, Attribute>> attributesByActionMap = new HashMap<>();
            for (String action : actions) {
                List<Attribute> attributesByAction = abacAuthModel.getPolicies().stream()
                        .filter(policy -> policy.getTarget().getAccessToActions().contains(action))
                        .flatMap(policy -> policy.getRules().stream()
                                .flatMap(rule -> rule.getConditions().stream()
                                        .flatMap(condition -> Stream.of(condition.getFirstOperand(), condition.getSecondOperandAttribute()).filter(Objects::nonNull))))
                        .distinct().collect(Collectors.toList());


                List<Attribute> attributeFromParams = attributesByAction.stream()
                        .flatMap(attribute -> datasources.stream()
                                .filter(datasource -> datasource.getReturnAttribute().equals(attribute))
                                .flatMap(datasource -> datasource.getParams().stream().map(param -> param.getAttributeParam())))
                        .distinct().collect(Collectors.toList());

                attributesByAction.addAll(attributeFromParams);

                Map<String, Attribute> collect = attributesByAction.stream().distinct().collect(Collectors.toMap(Attribute::getId, a -> a));

                attributesByActionMap.put(action, collect);
            }

            System.out.println(attributesByActionMap);

        }

        private void enrichDatasources(List<Datasource> datasources, AbacAuthModel abacAuthModel) throws EasyAbacInitException {
            for (Datasource datasource : datasources) {
                for (Param param : datasource.getParams()) {
                    Attribute attributeParam = findAttribute(abacAuthModel.getAttributes(), param.getAttributeParamId());
                    param.setAttributeParam(attributeParam);
                }

                Attribute requiredAttribute = findAttribute(abacAuthModel.getAttributes(), datasource.getReturnAttributeId());
                datasource.setReturnAttribute(requiredAttribute);
            }
        }

    }
}