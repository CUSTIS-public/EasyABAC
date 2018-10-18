package custis.easyabac.core.pdp.balana;

import custis.easyabac.model.AbacAuthModel;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

import static custis.easyabac.core.pdp.balana.trace.interceptors.cglib.CGLibPolicyElementsFactory.createAbstractPolicy;

/**
 * TODO: Write documentation for EasyPolicyFinderModule
 */
public class EasyPolicyFinderModule extends PolicyFinderModule {

    private final boolean useProxy;
    private final AbacAuthModel abacAuthModel;

    private AbstractPolicy policySet = null;
    private PolicyFinderResult policyFinderResult = null;

    EasyPolicyFinderModule(AbacAuthModel abacAuthModel, boolean useProxy) {
        this.abacAuthModel = abacAuthModel;
        this.useProxy = useProxy;
    }

    @Override
    public void init(PolicyFinder policyFinder) {
        this.policySet = new BalanaPolicyBuilder().buildFrom(abacAuthModel);
        if (useProxy) {
            this.policySet = createAbstractPolicy(policySet, policyFinder);
        }
        this.policyFinderResult = new PolicyFinderResult(policySet);
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        return policyFinderResult;
    }
}