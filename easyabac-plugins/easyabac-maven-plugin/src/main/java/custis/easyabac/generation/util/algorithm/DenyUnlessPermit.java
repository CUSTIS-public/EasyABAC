package custis.easyabac.generation.util.algorithm;

import custis.easyabac.api.test.TestDescription;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;
import custis.easyabac.generation.util.TestDataHolder;

import java.util.Collections;
import java.util.List;

public class DenyUnlessPermit implements TestGenerationAlgorithm {

    @Override
    public void generatePolicies(List<Policy> policies, TestDataHolder testDataHolder) {
        for (int i = 0; i < policies.size(); i++) {
            // permit tests
            Policy permitPolicy = policies.get(i);
            List<Policy> denyOrNA = Collections.emptyList();
            if (i > 0) {
                 denyOrNA = policies.subList(0, i);
            }
            //testDataHolder.addPermitTests(generateTests(permitPolicy, denyOrNA));
        }
    }

    private List<TestDescription> generateTests(Policy permitPolicy, List<Policy> denyOrNA) {

        return null;
    }

    @Override
    public void generateRules(List<Rule> rules, TestDataHolder testDataHolder) {

    }
}
