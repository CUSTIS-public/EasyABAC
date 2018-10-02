package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthRequest;
import custis.easyabac.pdp.MdpAuthResponse;
import custis.easyabac.pdp.RequestId;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.xacml3.Attributes;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

import static custis.easyabac.core.init.AttributesFactory.ATTRIBUTE_REQUEST_ID;
import static custis.easyabac.core.init.AttributesFactory.stringAttribute;
import static custis.easyabac.pdp.AuthResponse.Decision.getByIndex;

public class BalanaPdpHandler implements PdpHandler {

    private final PDP pdp;

    private BalanaPdpHandler(PDP pdp) {
        this.pdp = pdp;
    }

    @Override
    public AuthResponse evaluate(List<AttributeValue> attributeValues) {

        ResponseCtx responseCtx;
        try {
            Set<org.wso2.balana.ctx.Attribute> balanaAttributes = new HashSet<>();

            for (AttributeValue attributeValue : attributeValues) {
                org.wso2.balana.ctx.Attribute newBalanaAttribute = stringAttribute(attributeValue.getAttribute(), attributeValue.getValues());

                balanaAttributes.add(newBalanaAttribute);
            }

            Set<Attributes> attributesSet = new HashSet<>();
            //TODO  жестко задана категория!!!
            Attributes newBalanaAttributesSet = new Attributes(URI.create(Category.RESOURCE.getXacmlName()), balanaAttributes);
            attributesSet.add(newBalanaAttributesSet);

            RequestCtx requestCtx = new RequestCtx(attributesSet, null);

            requestCtx.encode(System.out);

//            requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request.getXacmlRequest().replaceAll(">\\s+<", "><"));
            responseCtx = pdp.evaluate(requestCtx);
        } catch (IllegalArgumentException e) {
            List<String> code = new ArrayList<>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            String error = "Invalid request  : " + e.getMessage();
            Status status = new Status(code, error);
            responseCtx = new ResponseCtx(new Result(AbstractResult.DECISION_INDETERMINATE, status));
        }
//        log.debug(responseCtx.encode());

        AuthResponse.Decision decision = getByIndex(responseCtx.getResults().iterator().next().getDecision());

        return new AuthResponse(decision);
    }

    @Override
    public MdpAuthResponse evaluate(MdpAuthRequest mdpAuthRequest) {
        Set<Attributes> attributesSet = new HashSet<>();
        RequestCtx requestCtx = new RequestCtx(null, attributesSet, false, false, multi, null);

        ResponseCtx responseCtx = pdp.evaluate(requestCtx);
        Map<RequestId, AuthResponse> results = new HashMap<>();

        for (AbstractResult abstractResult : responseCtx.getResults()) {
            Result result = (Result) abstractResult;

            for (Attributes attributes : result.getAttributes()) {
                for (Attribute attribute : attributes.getAttributes()) {
                    if (attribute.getId().equals(ATTRIBUTE_REQUEST_ID)) {
                        org.wso2.balana.attr.AttributeValue requestId = attribute.getValue();
                        results.put(RequestId.of(requestId.encode()), new AuthResponse(getByIndex(abstractResult.getDecision())));
                    }
                }
            }

        }

        return new MdpAuthResponse(results);
    }

    public static PdpHandler getInstance(AbacAuthModel abacAuthModel, List<SampleDatasource> datasources, Cache cache) {


        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();

        PDP pdp = new PDP(pdpConfig);


        return new BalanaPdpHandler(pdp);
    }


    public static PdpHandler getInstance(InputStream policyXacml, List<SampleDatasource> datasources, Cache cache) {
        PolicyFinder policyFinder = new PolicyFinder();

        PolicyFinderModule stringPolicyFinderModule = new InputStreamPolicyFinderModule(policyXacml);
        Set<PolicyFinderModule> policyModules = new HashSet<>();

        policyModules.add(stringPolicyFinderModule);
        policyFinder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();

        // registering new stringAttribute finder. so default PDPConfig is needed to change
        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();
        List<AttributeFinderModule> finderModules = attributeFinder.getModules();

        for (SampleDatasource datasource : datasources) {
            finderModules.add(new SampleAttributeFinderModule(datasource, cache));
        }
        attributeFinder.setModules(finderModules);

        PDP pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null, true));


        return new BalanaPdpHandler(pdp);
    }

}
