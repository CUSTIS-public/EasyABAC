package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthResponse;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

                List<org.wso2.balana.attr.AttributeValue> balanaAttributeValues = new ArrayList<>();

                for (String value : attributeValue.getValues()) {
                    balanaAttributeValues.add(new StringAttribute(value));
                }


                org.wso2.balana.ctx.Attribute newBalanaAttribute =
                        new org.wso2.balana.ctx.Attribute(new URI(attributeValue.getAttribute().getId()), new URI(StringAttribute.identifier),
                                "", null, balanaAttributeValues, false, 3);
                balanaAttributes.add(newBalanaAttribute);
            }

            Set<Attributes> attributesSet = new HashSet<>();
            //TODO  жестко задана категория!!!
            Attributes newBalanaAttributesSet = new Attributes(new URI(Category.RESOURCE.getXacmlName()), balanaAttributes);
            attributesSet.add(newBalanaAttributesSet);

            RequestCtx requestCtx = new RequestCtx(attributesSet, null);

            requestCtx.encode(System.out);

//            requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request.getXacmlRequest().replaceAll(">\\s+<", "><"));
            responseCtx = pdp.evaluate(requestCtx);
        } catch (URISyntaxException e) {
            List<String> code = new ArrayList<>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            String error = "Invalid request  : " + e.getMessage();
            Status status = new Status(code, error);
            responseCtx = new ResponseCtx(new Result(AbstractResult.DECISION_INDETERMINATE, status));
        }
//        log.debug(responseCtx.encode());

        AuthResponse.Decision decision = AuthResponse.Decision.getByIndex(responseCtx.getResults().iterator().next().getDecision());

        return new AuthResponse(decision);
    }

    @Override
    public MdpAuthResponse evaluate() {
        Set<Attributes> attributesSet = new HashSet<>();
        RequestCtx requestCtx = new RequestCtx(null, attributesSet, false, false, multi, null);

        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        return null;
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

        // registering new attribute finder. so default PDPConfig is needed to change
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
