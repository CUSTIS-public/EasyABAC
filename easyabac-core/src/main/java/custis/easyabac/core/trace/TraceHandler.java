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

/**
 * Holder of Trace process
 */
public class TraceHandler {
    public void onRuleEvaluateStart(Rule rule) {

    }

    public void onRuleEvaluateEnd(AbstractResult realResult) {

    }

    public void onRuleMatchStart(Rule rule) {

    }

    public void onRuleMatchEnd(MatchResult realResult) {

    }

    public void onConditionEvaluateStart(Condition condition) {

    }

    public void onConditionEvaluateEnd(EvaluationResult realResult) {

    }

    public void onFindPolicyStart() {

    }

    public void onFindPolicyEnd(PolicyFinderResult policyFinderResult) {

    }

    public void onRuleCombineStart(RuleCombiningAlgorithm combiningAlg) {

    }

    public void onRuleCombineEnd(AbstractResult result) {

    }

    public void onPolicyEvaluateStart(AbstractPolicy policy) {

    }

    public void onPolicyEvaluateEnd(AbstractResult realResult) {

    }

    public void onPolicyMatchStart(AbstractPolicy policy) {

    }

    public void onPolicyMatchEnd(MatchResult realResult) {

    }

    public void onPolicyCombineStart(PolicyCombiningAlgorithm combiningAlg) {

    }

    public void onPolicyCombineEnd(AbstractResult realResult) {

    }

    public void onFindAttribute(EvaluationResult invokeSuperResult) {

    }

    public void beforeProcess(EvaluationCtx arg) {

    }

    public void postProcess(ResponseCtx realResult) {

    }
}
