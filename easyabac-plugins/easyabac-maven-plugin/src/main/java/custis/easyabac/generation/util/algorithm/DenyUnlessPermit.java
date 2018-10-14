package custis.easyabac.generation.util.algorithm;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static custis.easyabac.generation.util.algorithm.CombinationAlgorithmFactory.getByCode;
import static custis.easyabac.generation.util.algorithm.FunctionUtils.ACTION;

public class DenyUnlessPermit implements TestGenerationAlgorithm {

    private static final Logger LOGGER = LoggerFactory.getLogger(DenyUnlessPermit.class);

    @Override
    public List<Map<String, String>> generatePolicies(List<Policy> policies, boolean expectedResult) throws EasyAbacInitException {
        List<Map<String, String>> tests = new ArrayList<>();
        if (expectedResult) {
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
            List<Map<String, String>> permitValues = generateValues(permitPolicy, true);
            for (Map<String, String> permitValue : permitValues) {
                permitValue.put(ACTION, accessToAction);
                LOGGER.info("Permitted value " + permitValue);
                LOGGER.info("Permitted policy[{}] - action [{}]", permitPolicy.getId(), accessToAction);
                // if action exists in previous policies then should be denied
                // finding these policies

                for (Policy policy : denyOrNA) {
                    if (policy.getTarget().getAccessToActions().contains(accessToAction)) {
                        LOGGER.info("Policy[{}] with equal action in target{}", policy.getId(), policy.getTarget().getAccessToActions());
                        TestGenerationAlgorithm ruleCombinationAlgorithm = getByCode(policy.getCombiningAlgorithm());
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
            List<Map<String, String>> deniedValues = generateValues(denyPolicy, false);
            for (Map<String, String> denyValue : deniedValues) {
                denyValue.put(ACTION, accessToAction);
                LOGGER.info("Denied value " + denyValue);
                LOGGER.info("Denied policy[{}] - action [{}]", denyPolicy.getId(), accessToAction);
                // if action exists in previous policies then should be denied
                // finding these policies

                for (Policy policy : permitOrNA) {
                    if (policy.getTarget().getAccessToActions().contains(accessToAction)) {
                        LOGGER.info("Policy[{}] with equal action in target{}", policy.getId(), policy.getTarget().getAccessToActions());
                        TestGenerationAlgorithm ruleCombinationAlgorithm = getByCode(policy.getCombiningAlgorithm());
                    }
                }
            }
            out.addAll(deniedValues);
        }
        return out;
    }

    /**
     * Different cases to generate permit for policy
     * @param permitPolicy
     */
    private List<Map<String, String>> generateValues(Policy permitPolicy, boolean expectedResult) throws EasyAbacInitException {
        TestGenerationAlgorithm ruleCombinationAlgorithm = getByCode(permitPolicy.getCombiningAlgorithm());
        return ruleCombinationAlgorithm.generateRules(permitPolicy.getRules(), expectedResult);
    }

    @Override
    public List<Map<String, String>> generateRules(List<Rule> rules, boolean expectedResult) throws EasyAbacInitException {
        List<Map<String, String>> values = new ArrayList<>();


        Rule rule = rules.get(0); // FIXME not so dummy
        if (expectedResult) {
            values = RuleGenerationUtils.generateRule(rule, expectedResult, new HashMap<>());
        } else {
            values = RuleGenerationUtils.generateRule(rule, expectedResult, new HashMap<>());
        }
        return values;
    }

}
