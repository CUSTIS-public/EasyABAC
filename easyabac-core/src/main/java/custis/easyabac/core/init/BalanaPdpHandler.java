package custis.easyabac.core.init;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.model.abac.attribute.AttributeGroup;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.trace.BalanaTraceHandler;
import custis.easyabac.core.trace.BalanaTraceHandlerProvider;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.core.trace.result.TraceResult;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthRequest;
import custis.easyabac.pdp.MdpAuthResponse;
import custis.easyabac.pdp.RequestId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.PDP;
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

import static custis.easyabac.core.init.AttributesFactory.ATTRIBUTE_REQUEST_ID;
import static custis.easyabac.core.init.AttributesFactory.balanaAttribute;
import static custis.easyabac.core.trace.BalanaTraceHandlerProvider.instantiate;
import static custis.easyabac.pdp.AuthResponse.Decision.getByIndex;
import static java.util.stream.Collectors.toSet;

public class BalanaPdpHandler implements PdpHandler {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdp;
    private final Trace trace;

    public BalanaPdpHandler(PDP pdp, Trace trace) {
        this.pdp = pdp;
        this.trace = trace;
    }

    @Override
    public AuthResponse evaluate(List<AttributeWithValue> attributeWithValues) {
        Map<Category, Attributes> attributesSet = new HashMap<>();

        for (AttributeWithValue attributeWithValue : attributeWithValues) {
            Category cat = attributeWithValue.getAttribute().getCategory();
            Attributes attributes = attributesSet.computeIfAbsent(cat,
                    category -> new Attributes(URI.create(category.getXacmlName()), new HashSet<>())
            );

            Attribute newBalanaAttribute = null;
            try {
                newBalanaAttribute = transformAttributeValue(attributeWithValue);
            } catch (EasyAbacInitException e) {
                return new AuthResponse(e.getMessage());
            }
            attributes.getAttributes().add(newBalanaAttribute);
        }

        RequestCtx requestCtx = new RequestCtx(new HashSet<>(attributesSet.values()), null);

        if (log.isDebugEnabled()) {
            requestCtx.encode(System.out);
        }

        BalanaTraceHandler balanaTraceHandler = instantiate(trace);
        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        if (log.isDebugEnabled()) {
            log.debug(responseCtx.encode());
        }

        return createResponse(responseCtx.getResults().iterator().next(), BalanaTraceHandlerProvider.get().getResult());
    }

    @Override
    public MdpAuthResponse evaluate(MdpAuthRequest mdpAuthRequest) {
        Set<RequestReference> requestReferences = mdpAuthRequest.getRequests()
                .stream()
                .map(this::transformReference)
                .collect(toSet());

        Set<Attributes> attributesSet = mdpAuthRequest.getAttributeGroups()
                .stream()
                .map(this::transformGroup)
                .collect(toSet());

        MultiRequests multiRequests = new MultiRequests(requestReferences);


        RequestCtx requestCtx = new RequestCtx(null, attributesSet, false, false, multiRequests, null);

        BalanaTraceHandler balanaTraceHandler = instantiate(trace);
        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        Map<RequestId, AuthResponse> results = new HashMap<>();

        for (AbstractResult abstractResult : responseCtx.getResults()) {
            Result result = (Result) abstractResult;

            Stream<Attributes> envAttributes = result.getAttributes()
                    .stream()
                    .filter(attributes -> attributes.getCategory().toString().equals(Category.ENV.getXacmlName()));

            Optional<Attribute> requestId = envAttributes.flatMap(attributes -> attributes.getAttributes().stream())
                    .filter(attribute -> attribute.getId().equals(ATTRIBUTE_REQUEST_ID))
                    .findFirst();

            if (!requestId.isPresent()) {
                throw new RuntimeException("Not found requestId in response");
            }

            results.put(RequestId.of(requestId.get().encode()), createResponse(abstractResult, BalanaTraceHandlerProvider.get().getResult()));

        }

        return new MdpAuthResponse(results);
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

    private Attributes transformGroup(AttributeGroup attributeGroup) {
//        Set<Attribute> attributeSet = attributeGroup.getAttributes()
//                .stream()
//                .map(this::transformAttributeValue)
//                .collect(toSet());
//
//        URI catUri = URI.create(attributeGroup.getCategory().getXacmlName());
//        return new Attributes(catUri, null, attributeSet, attributeGroup.getId());
        return null;
    }

    private Attribute transformAttributeValue(AttributeWithValue attributeWithValue) throws EasyAbacInitException {

        custis.easyabac.core.model.abac.attribute.Attribute attribute = attributeWithValue.getAttribute();
        return balanaAttribute(attribute.getXacmlName(), attribute.getType(), attributeWithValue.getValues(), false);

    }

    private RequestReference transformReference(MdpAuthRequest.RequestReference requestReference) {
        Set<AttributesReference> references = requestReference.getRequestIds()
                .stream()
                .map(r -> {
                    AttributesReference ar = new AttributesReference();
                    ar.setId(r);
                    return ar;
                })
                .collect(toSet());

        RequestReference ref = new RequestReference();
        ref.setReferences(references);
        return ref;
    }

}
