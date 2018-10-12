package custis.easyabac.core.trace.model;

import custis.easyabac.core.init.BalanaModelTransformer;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Policy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Policy with trace
 */
public class CalculatedPolicy extends AbstractCalculatedPolicy {

    private Policy policy;
    private List<CalculatedRule> rules = new ArrayList<>();

    public CalculatedPolicy(URI id) {
       super(id);
    }

    public void addRule(CalculatedRule calculatedRule) {
        rules.add(calculatedRule);
    }

    public List<CalculatedRule> getRules() {
        return rules;
    }

    public Policy getPolicy() {
        return policy;
    }

    @Override
    public void populateByModel(AbacAuthModel abacAuthModel) {
        Optional<Policy> mappedPolicy = abacAuthModel.getPolicies()
                .stream()
                .filter(policy1 -> policy1.getId().equals(BalanaModelTransformer.clearBalanaNamespace(id)))
                .findFirst();
        if (mappedPolicy.isPresent()) {
            this.policy = mappedPolicy.get();
        }

        rules.forEach(calculatedRule -> calculatedRule.populateByModel(abacAuthModel));
    }
}