package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.AbacAuthModel;
import org.wso2.balana.finder.PolicyFinder;

import java.util.Collections;

/**
 * TODO: Write documentation for EasyPolicyFinderModule
 */
public class EasyPolicyFinderModule extends EasyAbacBasePolicyFinderModule {

    private AbacAuthModel abacAuthModel;

    public EasyPolicyFinderModule(AbacAuthModel abacAuthModel) {
        this.abacAuthModel = abacAuthModel;
        this.policies = Collections.emptyMap();
    }

    @Override
    public void init(PolicyFinder policyFinder) {
        this.policies = new BalanaPolicyBuilder().buildFrom(abacAuthModel);
    }
}