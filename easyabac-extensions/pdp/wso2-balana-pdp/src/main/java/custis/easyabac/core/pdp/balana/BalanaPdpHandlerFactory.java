package custis.easyabac.core.pdp.balana;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.pdp.PdpHandler;
import custis.easyabac.core.pdp.PdpHandlerFactory;
import custis.easyabac.core.pdp.balana.policy.DatasourceAttributeFinderModule;
import custis.easyabac.core.pdp.balana.policy.InputStreamPolicyFinderModule;
import custis.easyabac.core.pdp.balana.trace.interceptors.cglib.CGLibPolicyElementsFactory;
import custis.easyabac.model.AbacAuthModel;
import org.wso2.balana.PDP;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.CurrentEnvModule;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BalanaPdpHandlerFactory implements PdpHandlerFactory {

    public static final PdpHandlerFactory DIRECT_INSTANCE = new BalanaPdpHandlerFactory(false);
    public static final PdpHandlerFactory PROXY_INSTANCE = new BalanaPdpHandlerFactory(true);

    private final boolean useProxy;

    public BalanaPdpHandlerFactory(boolean useProxy) {
        this.useProxy = useProxy;
    }

    @Override
    public boolean supportsXacmlPolicies() {
        return true;
    }

    @Override
    public PdpHandler newInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache) {
        return getInstance(new EasyPolicyFinderModule(abacAuthModel, useProxy), datasources, cache, useProxy, false);
    }

    @Override
    public PdpHandler newXacmlInstance(InputStream policyXacml, List<Datasource> datasources, Cache cache) {
        return getInstance(new InputStreamPolicyFinderModule(policyXacml, useProxy), datasources, cache, useProxy, true);
    }

    public static PdpHandler getInstance(PolicyFinderModule policyFinderModule, List<Datasource> datasources, Cache cache, boolean useProxy, boolean xacmlPolicyMode) {
        Set<PolicyFinderModule> policyModules = new HashSet<>();
        policyModules.add(policyFinderModule);

        List<AttributeFinderModule> finderModules = new ArrayList<>();
        for (Datasource datasource : datasources) {
            finderModules.add(new DatasourceAttributeFinderModule(datasource, cache));
        }

        finderModules.add(new CurrentEnvModule());

        PDP pdp = CGLibPolicyElementsFactory.newPDP(policyModules, finderModules, useProxy);

        return new BalanaPdpHandler(pdp, xacmlPolicyMode);
    }

}
