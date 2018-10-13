package custis.easyabac.core.trace.model;

import custis.easyabac.core.model.abac.Rule;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Policy with trace
 */
public class CalculatedRule {

    private final URI id;
    private Rule rule;
    private CalculatedResult result;
    private CalculatedMatch match;

    private List<CalculatedSimpleCondition> simpleConditions = new ArrayList<>();


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

    public List<CalculatedSimpleCondition> getSimpleConditions() {
        return simpleConditions;
    }

    public void addSimpleCondition(CalculatedSimpleCondition simpleCondition) {
        simpleConditions.add(simpleCondition);
    }

    public void populate(Rule rule) {
        this.rule = rule;

        for (int i = 0; i < this.rule.getConditions().size(); i++) {
            if (i > simpleConditions.size() - 1) {
                break;
            }
            simpleConditions.get(i).setCondition(this.rule.getConditions().get(i));
        }
    }
}
