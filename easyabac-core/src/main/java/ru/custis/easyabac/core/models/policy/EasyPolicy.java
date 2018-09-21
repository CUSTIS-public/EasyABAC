package ru.custis.easyabac.core.models.policy;

        import java.util.Map;

public class EasyPolicy {
    private String combiningAlgorithm = "deny-unless-permit";
    private Map<String, Policy> policies;

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
}
