package custis.easyabac.model;

import custis.easyabac.model.attribute.Attribute;

import java.util.List;
import java.util.Objects;

public class Policy {
    private final String id;
    private final String title;
    private final String combiningAlgorithm = "deny-unless-permit";
    ;
    private final Target target;
    private final List<Rule> rules;
    private final List<Attribute> returnAttributes;

    public Policy(String id, String title, Target target, List<Rule> rules, List<Attribute> returnAttributes) {
        this.id = id;
        this.title = title;
        this.target = target;
        this.rules = rules;
        this.returnAttributes = returnAttributes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCombiningAlgorithm() {
        return combiningAlgorithm;
    }

    public Target getTarget() {
        return target;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<Attribute> getReturnAttributes() {
        return returnAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Policy policy = (Policy) o;
        return Objects.equals(id, policy.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
