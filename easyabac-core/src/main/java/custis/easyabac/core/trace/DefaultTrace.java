package custis.easyabac.core.trace;

import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Rule;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.PolicyFinderResult;

public class DefaultTrace implements Trace {

    public static DefaultTrace INSTANCE = new DefaultTrace();

    @Override
    public void onRuleEvaluateStart(Rule rule) {

    }

    @Override
    public void onRuleEvaluateEnd(AbstractResult realResult) {

    }

    @Override
    public void onRuleMatchStart(Rule rule) {

    }

    @Override
    public void onRuleMatchEnd(MatchResult realResult) {

    }

    @Override
    public void onConditionEvaluateStart(Condition condition) {

    }

    @Override
    public void onConditionEvaluateEnd(EvaluationResult realResult) {

    }

    @Override
    public void onFindPolicyStart() {

    }

    @Override
    public void onFindPolicyEnd(PolicyFinderResult policyFinderResult) {

    }

    @Override
    public void onRuleCombineStart(RuleCombiningAlgorithm combiningAlg) {

    }

    @Override
    public void onRuleCombineEnd(AbstractResult result) {

    }

    @Override
    public void onPolicyEvaluateStart(AbstractPolicy policy) {

    }

    @Override
    public void onPolicyEvaluateEnd(AbstractResult realResult) {

    }

    @Override
    public void onPolicyMatchStart(AbstractPolicy policy) {

    }

    @Override
    public void onPolicyMatchEnd(MatchResult realResult) {

    }

    @Override
    public void onPolicyCombineStart(PolicyCombiningAlgorithm combiningAlg) {

    }

    @Override
    public void onPolicyCombineEnd(AbstractResult realResult) {

    }

    @Override
    public void onFindAttribute(EvaluationResult invokeSuperResult) {

    }

    @Override
    public void beforeProcess(EvaluationCtx arg) {

    }

    @Override
    public void postProcess(ResponseCtx realResult) {

    }
}
