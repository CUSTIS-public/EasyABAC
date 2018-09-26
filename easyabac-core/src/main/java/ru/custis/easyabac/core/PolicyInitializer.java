package ru.custis.easyabac.core;

import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import ru.custis.easyabac.core.models.attribute.Datasource;
import ru.custis.easyabac.core.models.attribute.load.EasyAttributeModel;
import ru.custis.easyabac.core.models.policy.EasyPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PolicyInitializer {

    public PDP getPDPNewInstance(String policyXacml) {

        PolicyFinder policyFinder = new PolicyFinder();

        PolicyFinderModule stringPolicyFinderModule = new StringPolicyFinderModule(policyXacml);
        Set<PolicyFinderModule> policyModules = new HashSet<>();

        policyModules.add(stringPolicyFinderModule);
        policyFinder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();

        // registering new attribute finder. so default PDPConfig is needed to change
        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();
        List<AttributeFinderModule> finderModules = attributeFinder.getModules();
        finderModules.add(new SampleAttributeFinderModule());
        attributeFinder.setModules(finderModules);

        return new PDP(new PDPConfig(null, policyFinder, null, true));
    }


    public PDP getPDPNewInstance(EasyPolicy easyPolicy, EasyAttributeModel easyAttributeModel, List<Datasource> datasources) {
            return getPDPNewInstance("");
    }

}
