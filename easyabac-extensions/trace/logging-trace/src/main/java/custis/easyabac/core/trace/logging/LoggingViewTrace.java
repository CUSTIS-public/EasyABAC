package custis.easyabac.core.trace.logging;

import custis.easyabac.core.trace.Trace;
import custis.easyabac.core.trace.model.*;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.Condition;
import custis.easyabac.model.Policy;
import custis.easyabac.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static custis.easyabac.model.easy.AuthModelTransformer.makeXacmlName;
import static custis.easyabac.model.easy.AuthModelTransformer.modeModelAttributeIdFromXacml;

public class LoggingViewTrace implements Trace {

    public static final Logger LOGGER = LoggerFactory.getLogger(LoggingViewTrace.class);
    public static final Trace INSTANCE = new LoggingViewTrace();

    @Override
    public void handleTrace(AbacAuthModel abacAuthModel, TraceResult traceResult) {
        StringBuilder modelWithTrace = new StringBuilder();
        modelWithTrace.append("\n").append("-------TRACE-------");
        modelWithTrace.append("\n--ATTRIBUTES--\n");
        if (traceResult == null) {
            modelWithTrace.append("\n NO TRACE RESULT \n");
            if (LOGGER.isDebugEnabled()) {
                //LOGGER.debug(modelWithTrace.toString());
            }
            return;
        }
        traceResult.getAttributes().forEach(
                (s, calculatedAttribute) -> {
                    modelWithTrace.append(modeModelAttributeIdFromXacml(calculatedAttribute.getId()))
                            .append(" = ").append(calculatedAttribute.getValues())
                            .append("\n");

                }
        );

        modelWithTrace.append("\n--GENERAL RESULT--\n");
        printTraceResult(traceResult, modelWithTrace);

        modelWithTrace.append("\n--CALC RESULT--");
        AbstractCalculatedPolicy mainPolicy = traceResult.getMainPolicy();
        if (mainPolicy instanceof CalculatedPolicySet) {
            List<CalculatedPolicy> calcPolicies = ((CalculatedPolicySet) mainPolicy).getPolicies();
            for (int i = 0; i < abacAuthModel.getPolicies().size(); i++) {
                Policy policy = abacAuthModel.getPolicies().get(i);
                if (i < calcPolicies.size() ) {
                    printPolicy(calcPolicies.get(i), 1, modelWithTrace, traceResult.getAttributes());
                } else {
                    printPolicyWithoutTrace(policy, 1, modelWithTrace);
                }
            }
        } else if (mainPolicy instanceof CalculatedPolicy) {
            printPolicy((CalculatedPolicy) mainPolicy, 1, modelWithTrace, traceResult.getAttributes());
        }
        //printTraceResult(traceResult);
        LOGGER.info(modelWithTrace.toString());
    }

    private void printPolicyWithoutTrace(Policy policy, int level, StringBuilder modelWithTrace) {
        modelWithTrace.append("\n");
        modelWithTrace.append(makeTabulation(level) + "Policy[" + policy.getId() + "]").append("\n");
        modelWithTrace.append(makeTabulation(level) + "title: " + policy.getTitle()).append("\n");
        modelWithTrace.append(makeTabulation(level) + "accessToActions" + policy.getTarget().getAccessToActions()).append("\n");
    }

    private void printTraceResult(TraceResult traceResult, StringBuilder modelWithTrace) {
        modelWithTrace.append("TraceResult for request[" + traceResult.getRequestId() + "]\n");
        if (traceResult.getMainPolicy() == null) {
            modelWithTrace.append("No policy found\n");
            return;
        }

        AbstractCalculatedPolicy mainPolicy = traceResult.getMainPolicy();
        if (mainPolicy instanceof CalculatedPolicy) {
            modelWithTrace.append("General result[" + mainPolicy.getId() + "] MATCH=" + mainPolicy.getMatch() + " RESULT=" + mainPolicy.getResult()).append("\n");
        } else if (mainPolicy instanceof CalculatedPolicySet) {
            modelWithTrace.append("General result[" + mainPolicy.getId() + "] MATCH=" + mainPolicy.getMatch() + " RESULT=" + mainPolicy.getResult()).append("\n");
        }
    }

    private void printPolicy(CalculatedPolicy policy, int level, StringBuilder modelWithTrace, Map<String, CalculatedAttribute> attributes) {
        Policy model = policy.getPolicy();
        modelWithTrace.append("\n");
        if (model != null) {
            modelWithTrace.append(makeTabulation(level) + "Policy[" + model.getId() + "]").append("\n");
            modelWithTrace.append(makeTabulation(level) + "title: " + model.getTitle()).append("\n");
            modelWithTrace.append(makeTabulation(level) + "accessToActions" + model.getTarget().getAccessToActions() + " MATCH=" + policy.getMatch()).append("\n");

        } else {
            modelWithTrace.append(makeTabulation(level) + "Policy[" + policy.getId() + "]").append("\n");
            modelWithTrace.append(makeTabulation(level) + "MATCH=" + policy.getMatch()).append("\n");
        }

        policy.getRules().forEach(calculatedRule -> printRule(calculatedRule, level + 1, modelWithTrace, attributes));
    }

    private void printRule(CalculatedRule rule, int level, StringBuilder modelWithTrace, Map<String, CalculatedAttribute> attributes) {
        Rule model = rule.getRule();
        if (model != null) {
            modelWithTrace.append(makeTabulation(level) + "Rule[" + model.getId() + "] MATCH=" + rule.getMatch() + " RESULT=" + rule.getResult()).append("\n");
        } else {
            modelWithTrace.append(makeTabulation(level) + "Rule[" + rule.getId() + "] MATCH=" + rule.getMatch() + " RESULT=" + rule.getResult()).append("\n");
        }

        for (CalculatedSimpleCondition calculatedSimpleCondition : rule.getSimpleConditions()) {
            printSimpleCondition(calculatedSimpleCondition, level + 1, modelWithTrace, attributes);
        }
    }

    private void printSimpleCondition(CalculatedSimpleCondition calculatedSimpleCondition, int level, StringBuilder modelWithTrace, Map<String, CalculatedAttribute> attributes) {
        Condition model = calculatedSimpleCondition.getCondition();
        if (model == null) {
            modelWithTrace.append(makeTabulation(level) + "Condition[" + calculatedSimpleCondition.getIndex() + "] RESULT=" + calculatedSimpleCondition.getResult()).append("\n");
        } else {
            modelWithTrace.append(makeTabulation(level) + prettyRepresentModelCondition(model) + " RESULT=" + calculatedSimpleCondition.getResult()).append("\n");
            modelWithTrace.append(makeTabulation(level + 1) + "BY VALUES: ") ;

            CalculatedAttribute calcFirst = attributes.get(makeXacmlName(model.getFirstOperand().getId()));
            if (calcFirst != null) {
                modelWithTrace.append(model.getFirstOperand().getId()).append(" ").append(calcFirst.getValues()).append(", ");
            } else {
                modelWithTrace.append(model.getFirstOperand().getId()).append(" [n/a]").append(", ");
            }

            if (model.getSecondOperandAttribute() != null) {
                CalculatedAttribute calcSecond = attributes.get(makeXacmlName(model.getSecondOperandAttribute().getId()));
                if (calcSecond != null) {
                    modelWithTrace.append(model.getSecondOperandAttribute().getId() + " " + calcSecond.getValues()).append("\n");
                }
            }
        }

    }

    private String prettyRepresentModelCondition(Condition model) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(model.getFirstOperand().getId()).append(" ").append(model.getFunction().getEasyName()).append(" ");
        if (model.getSecondOperandAttribute() != null) {
            stringBuilder.append(model.getSecondOperandAttribute().getId());
        } else {
            stringBuilder.append(model.getSecondOperandValue());
        }

        return stringBuilder.toString();
    }

    private String makeTabulation(int level) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            stringBuilder.append("--");
        }
        return stringBuilder.toString();
    }
}
