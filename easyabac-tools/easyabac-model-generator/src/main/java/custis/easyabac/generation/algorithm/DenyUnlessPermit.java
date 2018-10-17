package custis.easyabac.generation.algorithm;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.Effect;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static custis.easyabac.generation.algorithm.FunctionUtils.ACTION;
import static custis.easyabac.generation.algorithm.RuleGenerationUtils.generateRule;

public class DenyUnlessPermit implements TestGenerationAlgorithm {

    private static final Logger LOGGER = LoggerFactory.getLogger(DenyUnlessPermit.class);

    @Override
    public List<Map<String, String>> generatePolicies(List<Policy> policies, Effect expectedEffect) throws EasyAbacInitException {
        List<Map<String, String>> tests = new ArrayList<>();
        if (expectedEffect == Effect.PERMIT) {
            // we want permit
            for (int i = 0; i < policies.size(); i++) {
                // permit tests
                Policy permitPolicy = policies.get(i);
                List<Policy> denyOrNA = Collections.emptyList();
                if (i > 0) {
                    denyOrNA = policies.subList(0, i);
                    // others not important
                }
                tests.addAll(generatePermittedTests(permitPolicy, denyOrNA));
            }
        } else {
            // we want deny
            for (int i = 0; i < policies.size(); i++) {
                // permit tests
                Policy denyPolicy = policies.get(i);
                List<Policy> permitOrNA = Collections.emptyList();
                if (i > 0) {
                    permitOrNA = policies.subList(0, i);
                    // others not important
                }
                tests.addAll(generateDeniedTests(denyPolicy, permitOrNA));
            }
        }
        return tests;
    }

    private List<Map<String, String>> generatePermittedTests(Policy permitPolicy, List<Policy> denyOrNA) throws EasyAbacInitException {
        List<Map<String, String>> out = new ArrayList<>();

        List<String> accessToActions = permitPolicy.getTarget().getAccessToActions();
        // every action should be tested
        for (String accessToAction : accessToActions) {
            List<Map<String, String>> permitValues = generateValues(permitPolicy, Effect.PERMIT, new HashMap<>());
            for (Map<String, String> permitValue : permitValues) {
                permitValue.put(ACTION, accessToAction);
                LOGGER.info("Permitted value " + permitValue);
                LOGGER.info("Permitted policy[{}] - action [{}]", permitPolicy.getId(), accessToAction);
                // if action exists in previous policies then should be denied
                // finding these policies

                for (Policy policy : denyOrNA) {
                    if (policy.getTarget().getAccessToActions().contains(accessToAction)) {
                        LOGGER.info("Policy[{}] with equal action in target{}", policy.getId(), policy.getTarget().getAccessToActions());
                        generateValues(policy, Effect.DENY, new HashMap<>()); // FIXME сделать комбинацию
                    }
                }
            }
            out.addAll(permitValues);
        }
        return out;
    }

    private List<Map<String, String>> generateDeniedTests(Policy denyPolicy, List<Policy> permitOrNA) throws EasyAbacInitException {
        List<Map<String, String>> out = new ArrayList<>();

        List<String> accessToActions = denyPolicy.getTarget().getAccessToActions();
        // every action should be tested
        for (String accessToAction : accessToActions) {
            List<Map<String, String>> deniedValues = generateValues(denyPolicy, Effect.DENY, new HashMap<>());
            for (Map<String, String> denyValue : deniedValues) {
                denyValue.put(ACTION, accessToAction);
                LOGGER.info("Denied value " + denyValue);
                LOGGER.info("Denied policy[{}] - action [{}]", denyPolicy.getId(), accessToAction);
                // if action exists in previous policies then should be denied
                // finding these policies

                for (Policy policy : permitOrNA) {
                    if (policy.getTarget().getAccessToActions().contains(accessToAction)) {
                        LOGGER.info("Policy[{}] with equal action in target{}", policy.getId(), policy.getTarget().getAccessToActions());
                        generateValues(policy, Effect.PERMIT, new HashMap<>()); // FIXME сделать комбинацию
                    }
                }
            }
            out.addAll(deniedValues);
        }
        return out;
    }

    /**
     * Different cases to generate permit for policy
     * @param policy
     */
    private List<Map<String, String>> generateValues(Policy policy, Effect expectedEffect, Map<String, String> existingValues) throws EasyAbacInitException {
        TestGenerationAlgorithm ruleCombinationAlgorithm = CombinationAlgorithmFactory.getByCode(policy.getCombiningAlgorithm());
        return ruleCombinationAlgorithm.generateRules(policy.getRules(), expectedEffect, existingValues);
    }

    @Override
    public List<Map<String, String>> generateRules(List<Rule> rules, Effect expectedEffect, Map<String, String> existingValues) throws EasyAbacInitException {
        List<Map<String, String>> values = new ArrayList<>();


        if (expectedEffect == Effect.PERMIT) {
            for (int i = 0; i < rules.size(); i++) {
                // permit tests
                Rule rule = rules.get(i);
                List<Map<String, String>> satisfiedRules = generateRule(rule, rule.getEffect() == expectedEffect, existingValues);
                if (i > 0) {
                    List<Rule> previousRules = rules.subList(0, i);
                    // others not important
                    for (Map<String, String> satisfiedRule : satisfiedRules) {
                        for (Rule previousRule : previousRules) {
                            values.addAll(generateRule(previousRule, previousRule.getEffect() != expectedEffect, satisfiedRule));
                        }
                    }
                } else {
                    values.addAll(satisfiedRules);
                }
            }
        } else {
            List<Map<String, String>> satisfiedRules = new ArrayList<>();
            for (int i = 0; i < rules.size(); i++) {
                Rule rule = rules.get(i);
                if (satisfiedRules.isEmpty()) {
                    satisfiedRules = generateRule(rule, rule.getEffect() == expectedEffect, existingValues);
                } else {
                    List<Map<String, String>> newSatisfiedRules = new ArrayList<>();
                    for (Map<String, String> satisfiedRule : satisfiedRules) {
                        newSatisfiedRules.addAll(generateRule(rule, rule.getEffect() == expectedEffect, satisfiedRule));
                    }
                    satisfiedRules = newSatisfiedRules;
                }

            }
            values = satisfiedRules;
        }


        return values;
    }

}
