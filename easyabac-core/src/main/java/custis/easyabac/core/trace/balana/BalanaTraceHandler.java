package custis.easyabac.core.trace.balana;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import custis.easyabac.core.trace.model.*;
import custis.easyabac.pdp.RequestId;
import org.wso2.balana.*;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.PolicyFinderResult;

import java.net.URI;
import java.util.*;

import static custis.easyabac.core.init.AttributesFactory.ATTRIBUTE_REQUEST_ID;

/**
 * Holder of Trace process
 */
public class BalanaTraceHandler {

    private final Stack<Object> callStack = new Stack<Object>();
    private final Map<Attribute, BagAttribute> attributeMap = new HashMap<Attribute, BagAttribute>();
    private final Map<Attribute, Status> attributesStatusMap = new HashMap<Attribute, Status>();

    private Map<RequestId, TraceResult> traceResults = new LinkedHashMap<>();

    public void onFindPolicyStart(EvaluationCtx evaluationCtx) {
        // NEW part of request

        if (!callStack.empty()) {
            finalizeTraceResult();
        }

        EvaluationResult evalCtx = evaluationCtx.getAttribute(URI.create(DataType.STRING.getXacmlName()), ATTRIBUTE_REQUEST_ID, "", URI.create(Category.ENV.getXacmlName()));
        RequestId requestId = null;
        if (!evalCtx.indeterminate()) {
            List values = evalCtx.getAttributeValue().getChildren();
            if (!values.isEmpty()) {
                requestId = RequestId.of(values.get(0).toString());
            }
        }

        TraceResult traceResult = new TraceResult(requestId);
        traceResults.put(requestId, traceResult);
        callStack.push(evaluationCtx);
        callStack.push(traceResult);

    }

    private void finalizeTraceResult() {
        TraceResult traceResult = (TraceResult) callStack.pop();
        EvaluationCtx evalCtx = (EvaluationCtx) callStack.pop();
        System.out.println(evalCtx);
    }

    public void onFindPolicyEnd(PolicyFinderResult policyFinderResult) {
        // nothing to do
    }









    public void onPolicyMatchStart(AbstractPolicy policy) {
        Object top = callStack.peek();
        if (policy instanceof PolicySet) {
            CalculatedPolicySet ps = new CalculatedPolicySet(policy.getId().toString());
            if (top instanceof CalculatedPolicySet) {
                ((CalculatedPolicySet) top).addInnerResult(ps);
            } else if (top instanceof TraceResult) {
                ((TraceResult) top).setMainPolicy(ps);
            }
            callStack.push(ps);
        } else if (policy instanceof Policy) {

            CalculatedPolicy newPolicy = new CalculatedPolicy(policy.getId().toString());
            if (top instanceof CalculatedPolicySet) {
                ((CalculatedPolicySet) top).addPolicy(newPolicy);
            }
            callStack.push(newPolicy);

        }


    }

    public void onPolicyMatchEnd(MatchResult realResult) {
        AbstractCalculatedPolicy top = (AbstractCalculatedPolicy) callStack.peek();

        top.setMatch(CalculatedMatch.of(realResult.getResult()));
    }


    public void onPolicyEvaluateStart(AbstractPolicy abstractPolicy) {
        onPolicyMatchStart(abstractPolicy);
        onPolicyMatchEnd(new MatchResult(MatchResult.MATCH));
    }


    public void onRuleCombineStart(RuleCombiningAlgorithm combiningAlg) {
        // nothing to do
    }


    public void onRuleMatchStart(Rule rule) {
        CalculatedRule calcRule = new CalculatedRule(rule.getId().toString());
        Object policy = callStack.peek();
        if (policy instanceof CalculatedPolicy) {
            ((CalculatedPolicy) policy).addRule(calcRule);
        } else {
            throw new ProcessingException("Rule not inside Policy!");
        }

        callStack.push(calcRule);

    }

    public void onRuleMatchEnd(MatchResult realResult) {
        CalculatedRule rule = (CalculatedRule) callStack.peek();

        rule.setMatch(CalculatedMatch.of(realResult.getResult()));
    }

    public void onRuleEvaluateStart(Rule rule) {
        // nothing to do
        if (!(callStack.peek() instanceof CalculatedRule)) {
            onRuleMatchStart(rule);
            onRuleMatchEnd(new MatchResult(MatchResult.MATCH));
        }
    }

    public void onConditionEvaluateStart(Condition condition) {
        callStack.push(condition);
    }

    public void onConditionEvaluateEnd(EvaluationResult realResult) {
        Condition condition = (Condition) callStack.pop();
        // TODO may rule should be populated
    }

    public void onRuleEvaluateEnd(AbstractResult realResult) {
        CalculatedRule rule = (CalculatedRule) callStack.pop();
        rule.setResult(CalculatedResult.of(realResult.getDecision()));
    }


    public void onRuleCombineEnd(AbstractResult result) {
        // nothing to do
        ((CalculatedPolicy) callStack.peek()).setCombinationResult(CalculatedResult.of(result.getDecision()));
    }

    public void onPolicyEvaluateEnd(AbstractResult realResult) {
        AbstractCalculatedPolicy abstractCalculatedPolicy = (AbstractCalculatedPolicy) callStack.peek();
        abstractCalculatedPolicy.setResult(CalculatedResult.of(realResult.getDecision()));
        safePolicyEnd();
    }















    public void onPolicyCombineStart(PolicyCombiningAlgorithm combiningAlgorithm) {
        // nothing to do

    }

    public void onPolicyCombineEnd(AbstractResult result) {
        ((AbstractCalculatedPolicy) callStack.peek()).setCombinationResult(CalculatedResult.of(result.getDecision()));
    }

    public void onFindAttribute(EvaluationResult evaluationResult, String type, String attributeId, String category) throws EasyAbacInitException {
        Attribute attr = new Attribute(attributeId, Category.findByXacmlName(category), DataType.findByXacmlName(type));
        if (evaluationResult.indeterminate()) {
            attributesStatusMap.put(attr, evaluationResult.getStatus());
        } else {
            attributeMap.put(attr, (BagAttribute) evaluationResult.getAttributeValue());
        }
    }

    public void beforeProcess(RequestCtx evaluationCtx) {
        // nothing to do
    }

    public void postProcess(ResponseCtx realResult) {
        // nothing to do
        finalizeTraceResult();
    }

    private void safePolicyEnd() {
        Object stackTop = callStack.peek();
        if (stackTop instanceof CalculatedPolicy) {
            CalculatedPolicy policy = (CalculatedPolicy) callStack.pop();
        } else if (stackTop instanceof CalculatedPolicySet) {
            CalculatedPolicySet policySet = (CalculatedPolicySet) callStack.pop();
        }

    }



    public Map<RequestId, TraceResult> getResults() {
        return traceResults;
    }
}
