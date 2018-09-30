package custis.easyabac.core.model.policy;

import custis.easyabac.core.model.attribute.Attribute;

import java.util.List;
import java.util.Map;

public class AbacModel {
    private String combiningAlgorithm = "deny-unless-permit";

    private Map<String, Policy> policies;

    private List<Attribute> attributes;

    public Map<String, Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Map<String, Policy> policies) {
        this.policies = policies;
    }

    public String getCombiningAlgorithm() {
        return combiningAlgorithm;
    }

    public void setCombiningAlgorithm(String combiningAlgorithm) {
        this.combiningAlgorithm = combiningAlgorithm;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
