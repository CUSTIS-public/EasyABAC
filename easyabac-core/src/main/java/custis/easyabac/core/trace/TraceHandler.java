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
        System.out.println("onRuleEvaluateStart");
    }

    public void onRuleEvaluateEnd(AbstractResult realResult) {
        System.out.println("onRuleEvaluateEnd");
    }

    public void onRuleMatchStart(Rule rule) {
        System.out.println("onRuleMatchStart");
    }

    public void onRuleMatchEnd(MatchResult realResult) {
        System.out.println("onRuleMatchEnd");
    }

    public void onConditionEvaluateStart(Condition condition) {
        System.out.println("onConditionEvaluateStart");
    }

    public void onConditionEvaluateEnd(EvaluationResult realResult) {
        System.out.println("onConditionEvaluateEnd");
    }

    public void onFindPolicyStart() {
        System.out.println("onFindPolicyStart");
    }

    public void onFindPolicyEnd(PolicyFinderResult policyFinderResult) {
        System.out.println("onFindPolicyEnd");
    }

    public void onRuleCombineStart(RuleCombiningAlgorithm combiningAlg) {
        System.out.println("onRuleCombineStart");
    }

    public void onRuleCombineEnd(AbstractResult result) {
        System.out.println("onRuleCombineEnd");
    }

    public void onPolicyEvaluateStart(AbstractPolicy policy) {
        System.out.println("onPolicyEvaluateStart");
    }

    public void onPolicyEvaluateEnd(AbstractResult realResult) {
        System.out.println("onPolicyEvaluateEnd");
    }

    public void onPolicyMatchStart(AbstractPolicy policy) {
        System.out.println("onPolicyMatchStart");
    }

    public void onPolicyMatchEnd(MatchResult realResult) {
        System.out.println("onPolicyMatchEnd");
    }

    public void onPolicyCombineStart(PolicyCombiningAlgorithm combiningAlg) {
        System.out.println("onPolicyCombineStart");
    }

    public void onPolicyCombineEnd(AbstractResult realResult) {
        System.out.println("onPolicyCombineEnd");
    }

    public void onFindAttribute(EvaluationResult invokeSuperResult) {
        System.out.println("onFindAttribute");
    }

    public void beforeProcess(EvaluationCtx arg) {
        System.out.println("beforeProcess");
    }

    public void postProcess(ResponseCtx realResult) {
        System.out.println("postProcess");
    }
}
