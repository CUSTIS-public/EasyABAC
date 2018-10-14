package custis.easyabac.generation.util.algorithm;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;

import java.util.List;
import java.util.Map;

public interface TestGenerationAlgorithm {
    List<Map<String, String>> generatePolicies(final List<Policy> policies, boolean expectedResult) throws EasyAbacInitException;

    List<Map<String, String>> generateRules(List<Rule> rules, boolean expectedResult) throws EasyAbacInitException;

}
