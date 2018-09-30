package custis.easyabac.core.init;

import custis.easyabac.core.model.policy.EasyPolicy;
import org.wso2.balana.finder.PolicyFinder;

import java.util.Collections;
import java.util.Properties;

/**
 * TODO: Write documentation for EasyPolicyFinderModule
 */
public class EasyPolicyFinderModule extends EasyAbacBasePolicyFinderModule {

    private EasyPolicy easyPolicy;

    public EasyPolicyFinderModule(EasyPolicy easyPolicy) {
        this.easyPolicy = easyPolicy;
        this.policies = Collections.emptyMap();
    }

    @Override
    public void init(PolicyFinder policyFinder) {
        //TODO pass properties for builder
        this.policies = new EasyPolicyBuilder(new Properties()).buildFrom(easyPolicy);
    }
}