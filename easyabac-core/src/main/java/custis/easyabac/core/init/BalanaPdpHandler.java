package custis.easyabac.core.init;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.AttributeGroup;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.pdp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.xacml3.*;

import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static custis.easyabac.core.init.AttributesFactory.ATTRIBUTE_REQUEST_ID;
import static custis.easyabac.core.init.AttributesFactory.balanaAttribute;
import static custis.easyabac.pdp.AuthResponse.Decision.getByIndex;
import static java.util.stream.Collectors.toSet;

public class BalanaPdpHandler implements PdpHandler {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdp;

    private BalanaPdpHandler(PDP pdp) {
        this.pdp = pdp;
    }

    @Override
    public AuthResponse evaluate(List<AttributeWithValue> attributeWithValues) {
        Map<Category, Attributes> attributesMap;

        try {
            attributesMap = getBalanaAttributesByCategory(attributeWithValues, null);
        } catch (EasyAbacInitException e) {
            return new AuthResponse(e.getMessage());
        }
        RequestCtx requestCtx = new RequestCtx(new HashSet<>(attributesMap.values()), null);

        if (log.isDebugEnabled()) {
            requestCtx.encode(System.out);
        }
        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        if (log.isDebugEnabled()) {
            log.debug(responseCtx.encode());
        }

        return createResponse(responseCtx.getResults().iterator().next());
    }

    private Map<Category, Attributes> getBalanaAttributesByCategory(List<AttributeWithValue> attributeWithValues, String requestId) throws EasyAbacInitException {
        Map<Category, Attributes> attributesMap = new HashMap<>();

        for (AttributeWithValue attributeWithValue : attributeWithValues) {
            Category cat = attributeWithValue.getAttribute().getCategory();
            //не соберется
            Attributes attributes = attributesMap.computeIfAbsent(cat,
                    category -> new Attributes(URI.create(category.getXacmlName()), null, new HashSet<>(), requestId)
            );
            Attribute newBalanaAttribute = transformAttributeValue(attributeWithValue);

            attributes.getAttributes().add(newBalanaAttribute);
        }

        return attributesMap;
    }


    @Override
    public MdpAuthResponse evaluate(MultiAuthRequest multiAuthRequest) throws EasyAbacInitException {

        Set<RequestReference> requestReferences = new HashSet<>();

        for (String requestId : multiAuthRequest.getRequests().keySet()) {
            RequestReference requestReference = transformReference(requestId, multiAuthRequest.getRequests().get(requestId));
            requestReferences.add(requestReference);

            Map<Category, Attributes> balanaAttributesByCategory = getBalanaAttributesByCategory(multiAuthRequest.getRequests().get(requestId), requestId);
        }
        MultiRequests multiRequests = new MultiRequests(requestReferences);


        RequestCtx requestCtx = new RequestCtx(null, attributesSet, false, false, multiRequests, null);

        ResponseCtx responseCtx = pdp.evaluate(requestCtx);


        return null;
    }

    private Attributes transformGroup(AttributeGroup attributeGroup) throws EasyAbacInitException {
        Set<Attribute> attributeSet = new HashSet<>();
        for (AttributeWithValue attributeWithValue : attributeGroup.getAttributes()) {
            Attribute attribute = transformAttributeValue(attributeWithValue);
            attributeSet.add(attribute);
        }

        URI catUri = URI.create(attributeGroup.getCategory().getXacmlName());
        return new Attributes(catUri, null, attributeSet, attributeGroup.getId());
    }


    private Attribute transformAttributeValue(AttributeWithValue attributeWithValue) throws EasyAbacInitException {

        custis.easyabac.core.model.abac.attribute.Attribute attribute = attributeWithValue.getAttribute();
        return balanaAttribute(attribute.getXacmlName(), attribute.getType(), attributeWithValue.getValues(), false);
    }


    private RequestReference transformReference(String requestId, List<AttributeWithValue> attributeWithValues) {
        Set<AttributesReference> references = attributeWithValues
                .stream()
                .map(r -> {
                    AttributesReference ar = new AttributesReference();
                    ar.setId(r.getAttribute().getXacmlName());
                    return ar;
                })
                .collect(toSet());

        AttributesReference requestReference = new AttributesReference();
        requestReference.setId(ATTRIBUTE_REQUEST_ID.toString());
        references.add(requestReference);

        RequestReference ref = new RequestReference();
        ref.setReferences(references);
        return ref;
    }


    @Override
    public MdpAuthResponse evaluate(MdpAuthRequest mdpAuthRequest) {

        Set<RequestReference> requestReferences = mdpAuthRequest.getRequests()
                .stream()
                .map(this::transformReference)
                .collect(toSet());
        Set<Attributes> attributesSet;
        try {

            attributesSet = new HashSet<>();
            for (AttributeGroup attributeGroup : mdpAuthRequest.getAttributeGroups()) {
                Attributes attributes = transformGroup(attributeGroup);
                attributesSet.add(attributes);
            }
        } catch (EasyAbacInitException e) {
            return null;
        }

        MultiRequests multiRequests = new MultiRequests(requestReferences);


        RequestCtx requestCtx = new RequestCtx(null, attributesSet, false, false, multiRequests, null);

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

            results.put(RequestId.of(requestId.get().encode()), createResponse(abstractResult));

        }

        return new MdpAuthResponse(results);
    }

    private AuthResponse createResponse(AbstractResult abstractResult) {
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


        return new AuthResponse(decision, obligations);
    }

//    private Attributes transformGroup(AttributeGroup attributeGroup) throws EasyAbacInitException {
//        Set<Attribute> attributeSet = new HashSet<>();
//        for (AttributeWithValue attributeWithValue : attributeGroup.getAttributes()) {
//            Attribute attribute = transformAttributeValue(attributeWithValue);
//            attributeSet.add(attribute);
//        }
//
//        URI catUri = URI.create(attributeGroup.getCategory().getXacmlName());
//        return new Attributes(catUri, null, attributeSet, attributeGroup.getId());
//    }


//
//    private RequestReference transformReference(MdpAuthRequest.RequestReference requestReference) {
//        Set<AttributesReference> references = requestReference.getRequestIds()
//                .stream()
//                .map(r -> {
//                    AttributesReference ar = new AttributesReference();
//                    ar.setId(r);
//                    return ar;
//                })
//                .collect(toSet());
//
//        RequestReference ref = new RequestReference();
//        ref.setReferences(references);
//        return ref;
//    }

    public static PdpHandler getInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache) {


        PolicyFinder policyFinder = new PolicyFinder();

        PolicyFinderModule policyFinderModule = new EasyPolicyFinderModule(abacAuthModel);
        Set<PolicyFinderModule> policyModules = new HashSet<>();

        policyModules.add(policyFinderModule);
        policyFinder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();


        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();

        List<AttributeFinderModule> finderModules = attributeFinder.getModules();
        finderModules.clear();

        for (Datasource datasource : datasources) {
            finderModules.add(new DatasourceAttributeFinderModule(datasource, cache));
        }
        attributeFinder.setModules(finderModules);

        PDP pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null, true));

        return new BalanaPdpHandler(pdp);
    }


    public static PdpHandler getInstance(InputStream policyXacml, List<Datasource> datasources, Cache cache) {
        PolicyFinder policyFinder = new PolicyFinder();

        PolicyFinderModule stringPolicyFinderModule = new InputStreamPolicyFinderModule(policyXacml);
        Set<PolicyFinderModule> policyModules = new HashSet<>();

        policyModules.add(stringPolicyFinderModule);
        policyFinder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();

        // registering new attribute finder. so default PDPConfig is needed to change
        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();

        List<AttributeFinderModule> finderModules = attributeFinder.getModules();
        finderModules.clear();

        for (Datasource datasource : datasources) {
            finderModules.add(new DatasourceAttributeFinderModule(datasource, cache));
        }
        attributeFinder.setModules(finderModules);

        PDP pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null, true));


        return new BalanaPdpHandler(pdp);
    }

}
