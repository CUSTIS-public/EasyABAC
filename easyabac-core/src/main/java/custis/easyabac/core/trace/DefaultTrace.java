package custis.easyabac.core.trace;

import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.trace.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTrace implements Trace {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultTrace.class);
    public static final Trace INSTANCE = new DefaultTrace();

    @Override
    public void handleTrace(AbacAuthModel abacAuthModel, TraceResult traceResult) {
        printTraceResult(traceResult);
    }

    private void printTraceResult(TraceResult traceResult) {
        LOGGER.info("------------");
        LOGGER.info("TraceResult for request[" + traceResult.getRequestId() + "]");
        if (traceResult.getMainPolicy() == null) {
            LOGGER.info("No policy found");
            return;
        }

        AbstractCalculatedPolicy mainPolicy = traceResult.getMainPolicy();
        if (mainPolicy instanceof CalculatedPolicy) {
            LOGGER.info("General result[" + mainPolicy.getId() + "] MATCH=" + mainPolicy.getMatch() + " RESULT=" + mainPolicy.getResult());
            printInnerPolicy((CalculatedPolicy) mainPolicy, 1);
        } else if (mainPolicy instanceof CalculatedPolicySet) {
            LOGGER.info("General result[" + mainPolicy.getId() + "] MATCH=" + mainPolicy.getMatch() + " RESULT=" + mainPolicy.getResult());
            printInnerPolicySet((CalculatedPolicySet) mainPolicy, 1);
        }
    }

    private void printPolicySet(CalculatedPolicySet policy, int level) {
        LOGGER.info(makeTabulation(level) + "PolicySet[" + policy.getId() + "] MATCH=" + policy.getMatch() + " RESULT=" + policy.getResult());

        printInnerPolicySet(policy, level);
    }

    private void printInnerPolicySet(CalculatedPolicySet policy, int level) {
        policy.getInnerPolicySets().forEach(calculatedPolicySet -> printPolicySet(calculatedPolicySet, level + 1));

        policy.getPolicies().forEach(calculatedPolicy -> printPolicy(calculatedPolicy, level + 1));
    }

    private void printInnerPolicy(CalculatedPolicy policy, int level) {
        policy.getRules().forEach(calculatedRule -> printRule(calculatedRule, level + 1));
    }

    private void printPolicy(CalculatedPolicy policy, int level) {
        Policy model = policy.getPolicy();
        if (model != null) {
            LOGGER.info(makeTabulation(level) + "Policy[" + model.getId() + "]");
            LOGGER.info(makeTabulation(level) + "accessToActions" + model.getTarget().getAccessToActions() + " MATCH=" + policy.getMatch());
        } else {
            LOGGER.info(makeTabulation(level) + "Policy[" + policy.getId() + "]");
            LOGGER.info(makeTabulation(level) + "MATCH=" + policy.getMatch());
        }

        //+ " RESULT=" + policy.getResult())

        printInnerPolicy(policy, level);
    }

    private void printRule(CalculatedRule rule, int level) {
        LOGGER.info(makeTabulation(level) + "Rule[" + rule.getId() + "] MATCH=" + rule.getMatch() + " RESULT=" + rule.getResult());
    }

    private String makeTabulation(int level) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            stringBuilder.append("--");
        }
        return stringBuilder.toString();
    }
}
