package custis.easyabac.core.trace;

import custis.easyabac.core.trace.result.TraceResult;
import org.wso2.balana.*;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.PolicyFinderResult;

import java.util.Stack;

/**
 * Holder of Trace process
 */
public class BalanaTraceHandler {

    private final Trace trace;
    private final Stack<Object> callStack = new Stack<Object>();
    //private final Map<AttributeDescriptor, BagAttribute> attributeMap = new HashMap<AttributeDescriptor, BagAttribute>();
    //private final Map<AttributeDescriptor, Status> attributesStatusMap = new HashMap<AttributeDescriptor, Status>();


    public BalanaTraceHandler(Trace trace) {
        this.trace = trace;
    }

    public void onRuleEvaluateStart(Rule rule) {
        callStack.push(rule);
    }

    public void onRuleEvaluateEnd(AbstractResult realResult) {
        Rule rule = (Rule) callStack.pop();
    }

    public void onRuleMatchStart(Rule rule) {
        AbstractTarget target = rule.getTarget();
        callStack.push(target);
    }

    public void onRuleMatchEnd(MatchResult realResult) {
        AbstractTarget target = (AbstractTarget) callStack.pop();
    }

    public void onConditionEvaluateStart(Condition condition) {
        callStack.push(condition);
    }

    public void onConditionEvaluateEnd(EvaluationResult realResult) {
        Condition condition = (Condition) callStack.pop();
    }

    public void onFindPolicyStart() {
        System.out.println("onFindPolicyStart");
    }

    public void onFindPolicyEnd(PolicyFinderResult policyFinderResult) {
        System.out.println("onFindPolicyEnd");
    }

    public void onRuleCombineStart(RuleCombiningAlgorithm combiningAlg) {
        callStack.push(combiningAlg);
    }

    public void onRuleCombineEnd(AbstractResult result) {
        RuleCombiningAlgorithm combiningAlgorithm = (RuleCombiningAlgorithm) callStack.pop();
    }

    public void onPolicyEvaluateStart(AbstractPolicy abstractPolicy) {
        onPolicyMatchStart(abstractPolicy);
        onPolicyMatchEnd(new MatchResult(MatchResult.MATCH));
    }

    public void onPolicyEvaluateEnd(AbstractResult realResult) {
        safePolicyEnd();
    }

    public void onPolicyMatchStart(AbstractPolicy policy) {
        safePolicyEnd();

        callStack.push(policy);
        AbstractTarget target = policy.getTarget();
        callStack.push(target);

    }

    public void onPolicyMatchEnd(MatchResult realResult) {
        AbstractTarget target = (AbstractTarget) callStack.pop();
    }

    public void onPolicyCombineStart(PolicyCombiningAlgorithm combiningAlgorithm) {
        callStack.push(combiningAlgorithm);

    }

    public void onPolicyCombineEnd(AbstractResult realResult) {
        safePolicyEnd();
        PolicyCombiningAlgorithm combiningAlgorithm = (PolicyCombiningAlgorithm) callStack.pop();
    }

    public void onFindAttribute(EvaluationResult invokeSuperResult) {
        System.out.println("onFindAttribute");
    }

    public void beforeProcess(RequestCtx evaluationCtx) {
        callStack.push(evaluationCtx);
    }

    public void postProcess(ResponseCtx realResult) {
        RequestCtx evaluationCtx = (RequestCtx) callStack.pop();
    }

    private void safePolicyEnd() {
        Object stackTop = callStack.peek();
        if (stackTop instanceof AbstractPolicy) {
            AbstractPolicy policy = (AbstractPolicy) callStack.pop();
            if (policy instanceof Policy) {

            } else if (policy instanceof PolicySet) {

            } else if (policy instanceof PolicyReference) {

            }
        }
    }

    public TraceResult getResult() {
        return null;
    }
}
