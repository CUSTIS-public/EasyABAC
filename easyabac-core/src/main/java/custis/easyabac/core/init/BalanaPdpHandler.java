package custis.easyabac.core.init;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.model.IdGenerator;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import custis.easyabac.core.trace.balana.BalanaTraceHandler;
import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import custis.easyabac.core.trace.model.TraceResult;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MultiAuthRequest;
import custis.easyabac.pdp.MultiAuthResponse;
import custis.easyabac.pdp.RequestId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.PDP;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.xacml3.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static custis.easyabac.core.init.BalanaAttributesFactory.ATTRIBUTE_REQUEST_ID;
import static custis.easyabac.core.init.BalanaAttributesFactory.balanaAttribute;
import static custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider.instantiate;
import static custis.easyabac.pdp.AuthResponse.Decision.getByIndex;
import static java.util.stream.Collectors.toSet;

public class BalanaPdpHandler implements PdpHandler {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdp;
    private final boolean xacmlPolicyMode;

    public BalanaPdpHandler(PDP pdp, boolean xacmlPolicyMode) {
        this.pdp = pdp;
        this.xacmlPolicyMode = xacmlPolicyMode;
    }

    @Override
    public AuthResponse evaluate(List<AttributeWithValue> attributeWithValues) {

        RequestId requestId = RequestId.newRandom();
        addRequestIdAttribute(requestId, attributeWithValues);

        Map<Category, Attributes> attributesMap;

        try {
            attributesMap = getBalanaAttributesByCategory(attributeWithValues);
        } catch (EasyAbacInitException e) {
            return new AuthResponse(e.getMessage());
        }
        RequestCtx requestCtx = new RequestCtx(new HashSet<>(attributesMap.values()), null);

        if (log.isDebugEnabled()) {
            requestCtx.encode(System.out);
        }

        BalanaTraceHandler balanaTraceHandler = instantiate();
        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        if (log.isDebugEnabled()) {
            log.debug(responseCtx.encode());
        }

        Map<RequestId, TraceResult> results = BalanaTraceHandlerProvider.get().getResults();
        return createResponse(responseCtx.getResults().iterator().next(), results.get(requestId));
    }

    @Override
    public MultiAuthResponse evaluate(MultiAuthRequest multiAuthRequest) throws EasyAbacInitException {

        Set<RequestReference> requestReferences = new HashSet<>();
        Set<Attributes> attributesSet = new HashSet<>();
        for (RequestId requestId : multiAuthRequest.getRequests().keySet()) {

            List<AttributeWithValue> attributeWithValues = multiAuthRequest.getRequests().get(requestId);

            addRequestIdAttribute(requestId, attributeWithValues);

            Map<Category, Attributes> balanaAttributesByCategory = getBalanaAttributesByCategory(attributeWithValues);

            RequestReference requestReference = transformReference(balanaAttributesByCategory);
            requestReferences.add(requestReference);

            attributesSet.addAll(balanaAttributesByCategory.values());
        }
        MultiRequests multiRequests = new MultiRequests(requestReferences);


        RequestCtx requestCtx = new RequestCtx(null, attributesSet, false, false, multiRequests, null);
        BalanaTraceHandler balanaTraceHandler = instantiate();
        if (log.isDebugEnabled()) {
            requestCtx.encode(System.out);
        }

        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        if (log.isDebugEnabled()) {
            log.debug(responseCtx.encode());
        }

        Map<RequestId, AuthResponse> results = new HashMap<>();

        for (AbstractResult abstractResult : responseCtx.getResults()) {
            Result result = (Result) abstractResult;

            Stream<Attributes> envAttributes = result.getAttributes()
                    .stream()
                    .filter(attributes -> attributes.getCategory().toString().equals(Category.ENV.getXacmlName()));

            Optional<Attribute> requestId = envAttributes.flatMap(attributes -> attributes.getAttributes().stream())
                    .filter(attribute -> attribute.getId().toString().equals("request-id")).findFirst();

            if (!requestId.isPresent()) {
                throw new RuntimeException("Not found requestId in response");
            }

            StringAttribute value = (StringAttribute) requestId.get().getValue();

            Map<RequestId, TraceResult> traceResults = BalanaTraceHandlerProvider.get().getResults();
            results.put(RequestId.of(value.getValue()), createResponse(abstractResult, traceResults.get(RequestId.of(value.getValue()))));

        }

        return new MultiAuthResponse(results);
    }

    private void addRequestIdAttribute(RequestId requestId, List<AttributeWithValue> attributeWithValues) {
        AttributeWithValue requestIdAttribute = new AttributeWithValue(new custis.easyabac.core.model.abac.attribute.Attribute(ATTRIBUTE_REQUEST_ID, Category.ENV, DataType.STRING),
                Collections.singletonList(requestId.getId()));

        attributeWithValues.add(requestIdAttribute);
    }

    private RequestReference transformReference(Map<Category, Attributes> balanaAttributesByCategory) {
        Set<AttributesReference> references = new HashSet<>();
        for (Attributes attributes : balanaAttributesByCategory.values()) {
            AttributesReference reference = new AttributesReference();
            reference.setId(attributes.getId());
            references.add(reference);
        }
        RequestReference requestReference = new RequestReference();
        requestReference.setReferences(references);
        return requestReference;
    }

    private Map<Category, Attributes> getBalanaAttributesByCategory(List<AttributeWithValue> attributeWithValues) throws EasyAbacInitException {
        Map<Category, Attributes> attributesMap = new HashMap<>();

        for (AttributeWithValue attributeWithValue : attributeWithValues) {
            Category cat = attributeWithValue.getAttribute().getCategory();

            Attributes attributes = attributesMap.computeIfAbsent(cat,
                    category -> new Attributes(URI.create(category.getXacmlName()), null, new HashSet<>(), IdGenerator.newId())
            );
            boolean includeInResult = attributeWithValue.getAttribute().getId().equals(ATTRIBUTE_REQUEST_ID);


            Attribute newBalanaAttribute = transformAttributeValue(attributeWithValue, includeInResult);

            attributes.getAttributes().add(newBalanaAttribute);
        }

        return attributesMap;
    }


    private Attribute transformAttributeValue(AttributeWithValue attributeWithValue, boolean includeInResult) throws EasyAbacInitException {

        custis.easyabac.core.model.abac.attribute.Attribute attribute = attributeWithValue.getAttribute();
        return balanaAttribute(attribute.getXacmlName(), attribute.getType(), attributeWithValue.getValues(), includeInResult);
    }

    @Override
    public boolean xacmlPolicyMode() {
        return xacmlPolicyMode;
    }

    private AuthResponse createResponse(AbstractResult abstractResult, TraceResult traceResult) {
        AuthResponse.Decision decision = getByIndex(abstractResult.getDecision());
        Set<AttributeAssignment> assignments = abstractResult.getObligations()
                .stream()
                .filter(obligationResult -> obligationResult instanceof Obligation)
                .flatMap(obligationResult -> ((Obligation) obligationResult).getAssignments().stream())
                .collect(toSet());
        Map<String, String> obligations = new HashMap<>();
        for (AttributeAssignment assignment : assignments) {
            obligations.put(assignment.getAttributeId().toString(), assignment.getContent());
        }


        return new AuthResponse(decision, obligations, traceResult);
    }

}
