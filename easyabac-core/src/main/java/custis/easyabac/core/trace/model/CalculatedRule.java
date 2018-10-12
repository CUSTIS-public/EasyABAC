package custis.easyabac.core.trace.model;

import custis.easyabac.core.init.BalanaModelTransformer;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;

import java.net.URI;

/**
 * Policy with trace
 */
public class CalculatedRule implements Populatable {

    private final URI id;
    private Rule rule;
    private CalculatedResult result;
    private CalculatedMatch match;


    public CalculatedRule(URI id) {
        this.id = id;
    }

    public void setMatch(CalculatedMatch match) {
        this.match = match;
    }

    public void setResult(CalculatedResult result) {
        this.result = result;
    }

    public URI getId() {
        return id;
    }

    public CalculatedResult getResult() {
        return result;
    }

    public CalculatedMatch getMatch() {
        return match;
    }

    public Rule getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return "CalculatedRule{" +
                "id='" + id + '\'' +
                ", result=" + result +
                ", match=" + match +
                '}';
    }

    @Override
    public void populateByModel(AbacAuthModel abacAuthModel) {
        String policyAndRuleId = BalanaModelTransformer.clearBalanaNamespace(id);
        for (Policy policy : abacAuthModel.getPolicies()) {
            for (Rule rule1 : policy.getRules()) {
                if (policyAndRuleId.equals(BalanaModelTransformer.simpleRuleId(policy.getId(), rule1.getId()))) {
                    this.rule = rule1;
                    return;
                }
            }
        }
    }
}
