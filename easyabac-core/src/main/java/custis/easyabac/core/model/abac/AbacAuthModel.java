package custis.easyabac.core.model.abac;

import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.easy.EasyResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AbacAuthModel {
    private String combiningAlgorithm = "deny-unless-permit";

    private Map<String, Policy> policies = Collections.emptyMap();

    private Map<String, EasyResource> resources = Collections.emptyMap();

    private List<Attribute> attributes = Collections.emptyList();

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

    public Map<String, EasyResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, EasyResource> resources) {
        this.resources = resources;
    }
}
