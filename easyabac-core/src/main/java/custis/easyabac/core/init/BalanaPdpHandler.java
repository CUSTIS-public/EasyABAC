package custis.easyabac.core.init;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.AttributeGroup;
import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthRequest;
import custis.easyabac.pdp.MdpAuthResponse;
import custis.easyabac.pdp.RequestId;
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
import static custis.easyabac.core.init.AttributesFactory.stringAttribute;
import static custis.easyabac.pdp.AuthResponse.Decision.getByIndex;
import static java.util.stream.Collectors.toSet;

public class BalanaPdpHandler implements PdpHandler {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdp;

    private BalanaPdpHandler(PDP pdp) {
        this.pdp = pdp;
    }

    @Override
    public AuthResponse evaluate(List<AttributeValue> attributeValues) {

        ResponseCtx responseCtx;

        Map<Category, Attributes> attributesSet = new HashMap<>();

        for (AttributeValue attributeValue : attributeValues) {
            Category cat = attributeValue.getAttribute().getCategory();
            Attributes attributes = attributesSet.computeIfAbsent(cat,
                    category -> new Attributes(URI.create(category.getXacmlName()), new HashSet<>())
            );


            org.wso2.balana.ctx.Attribute newBalanaAttribute = stringAttribute(attributeValue.getAttribute(), attributeValue.getValues());
            attributes.getAttributes().add(newBalanaAttribute);
        }

        RequestCtx requestCtx = new RequestCtx(new HashSet<>(attributesSet.values()), null);

        requestCtx.encode(System.out);

        responseCtx = pdp.evaluate(requestCtx);

        if (log.isDebugEnabled()) {
            log.debug(responseCtx.encode());
        }

        return createResponse(responseCtx.getResults().iterator().next());
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

    private Attributes transformGroup(AttributeGroup attributeGroup) {
        Set<Attribute> attributeSet = attributeGroup.getAttributes()
                .stream()
                .map(this::transformAttributeValue)
                .collect(toSet());

        URI catUri = URI.create(attributeGroup.getCategory().getXacmlName());
        return new Attributes(catUri, null, attributeSet, attributeGroup.getId());
    }

    private Attribute transformAttributeValue(AttributeValue attributeValue) {
        return stringAttribute(attributeValue.getAttribute(), attributeValue.getValues());
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

    public static PdpHandler getInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache) {


        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();

        PDP pdp = new PDP(pdpConfig);


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

        for (Datasource datasource : datasources) {
            finderModules.add(new DatasourceAttributeFinderModule(datasource, cache));
        }
        attributeFinder.setModules(finderModules);

        PDP pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null, true));


        return new BalanaPdpHandler(pdp);
    }

}
