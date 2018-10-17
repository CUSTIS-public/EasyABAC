package custis.easyabac.generation.algorithm;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.Effect;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;

import java.util.List;
import java.util.Map;

public interface TestGenerationAlgorithm {
    List<Map<String, String>> generatePolicies(final List<Policy> policies, Effect expectedEffect) throws EasyAbacInitException;

    List<Map<String, String>> generateRules(List<Rule> rules, Effect expectedEffect, Map<String, String> existingValues) throws EasyAbacInitException;

}
