package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.AbacAuthModel;
import org.wso2.balana.PolicySet;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

/**
 * TODO: Write documentation for EasyPolicyFinderModule
 */
public class EasyPolicyFinderModule extends PolicyFinderModule {

    private AbacAuthModel abacAuthModel;

    private PolicySet policySet = null;

    EasyPolicyFinderModule(AbacAuthModel abacAuthModel) {
        this.abacAuthModel = abacAuthModel;
    }

    @Override
    public void init(PolicyFinder policyFinder) {
        this.policySet = new BalanaPolicyBuilder().buildFrom(abacAuthModel);
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        return new PolicyFinderResult(policySet);
    }
}