package custis.easyabac.generation.util.algorithm;

import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;
import custis.easyabac.generation.util.TestDataHolder;

import java.util.List;

public interface TestGenerationAlgorithm {
    void generatePolicies(final List<Policy> policies, final TestDataHolder testDataHolder);

    void generateRules(final List<Rule> rules, final TestDataHolder testDataHolder);
}
