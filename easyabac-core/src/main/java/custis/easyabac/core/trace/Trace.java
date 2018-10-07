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

public interface Trace {

    void beforeProcess(EvaluationCtx arg);
    void postProcess(ResponseCtx realResult);

    void onPolicyCombineStart(PolicyCombiningAlgorithm combiningAlg);
    void onPolicyCombineEnd(AbstractResult realResult);
    void onPolicyMatchStart(AbstractPolicy policy);
    void onPolicyMatchEnd(MatchResult realResult);
    void onPolicyEvaluateStart(AbstractPolicy policy);
    void onPolicyEvaluateEnd(AbstractResult realResult);

    void onRuleCombineStart(RuleCombiningAlgorithm combiningAlg);
    void onRuleCombineEnd(AbstractResult result);
    void onRuleMatchStart(Rule rule);
    void onRuleMatchEnd(MatchResult realResult);
    void onRuleEvaluateStart(Rule rule);
    void onRuleEvaluateEnd(AbstractResult realResult);

    void onConditionEvaluateStart(Condition condition);
    void onConditionEvaluateEnd(EvaluationResult realResult);

    void onFindPolicyStart();
    void onFindPolicyEnd(PolicyFinderResult policyFinderResult);

    void onFindAttribute(EvaluationResult invokeSuperResult);


}
