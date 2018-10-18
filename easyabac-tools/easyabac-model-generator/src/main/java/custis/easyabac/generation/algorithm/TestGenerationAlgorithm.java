package custis.easyabac.generation.algorithm;

import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.Effect;
import custis.easyabac.model.Policy;
import custis.easyabac.model.Rule;

import java.util.List;
import java.util.Map;

public interface TestGenerationAlgorithm {
    List<Map<String, String>> generatePolicies(final List<Policy> policies, Effect expectedEffect) throws EasyAbacInitException;

    List<Map<String, String>> generateRules(List<Rule> rules, Effect expectedEffect, Map<String, String> existingValues) throws EasyAbacInitException;

}
