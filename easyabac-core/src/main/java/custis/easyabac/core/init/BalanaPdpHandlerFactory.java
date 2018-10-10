package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BalanaPdpHandlerFactory implements PdpHandlerFactory {

    public static final PdpHandlerFactory INSTANCE = new BalanaPdpHandlerFactory();

    @Override
    public boolean supportsXacmlPolicies() {
        return true;
    }

    @Override
    public PdpHandler newInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache) {
        return getInstance(new EasyPolicyFinderModule(abacAuthModel), datasources, cache);
    }

    @Override
    public PdpHandler newXacmlInstance(InputStream policyXacml, List<Datasource> datasources, Cache cache) {
        return getInstance(new InputStreamPolicyFinderModule(policyXacml), datasources, cache);
    }

    public static PdpHandler getInstance(PolicyFinderModule policyFinderModule, List<Datasource> datasources, Cache cache) {
        PolicyFinder policyFinder = new PolicyFinder();
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

}
