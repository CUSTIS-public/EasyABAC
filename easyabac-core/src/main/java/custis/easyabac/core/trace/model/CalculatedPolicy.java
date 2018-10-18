package custis.easyabac.core.trace.model;

import custis.easyabac.model.Policy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

    public void populate(Policy policy) {
        this.policy = policy;

        for (int i = 0; i < policy.getRules().size(); i++) {
            if (i > rules.size() - 1) {
                break;
            }
            rules.get(i).populate(policy.getRules().get(i));
        }
    }
}