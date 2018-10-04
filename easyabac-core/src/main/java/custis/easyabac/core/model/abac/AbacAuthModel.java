package custis.easyabac.core.model.abac;

import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Resource;

import java.util.List;
import java.util.Map;

public class AbacAuthModel {
    private final String combiningAlgorithm = "deny-unless-permit";
    private final List<Policy> policies;
    private final Map<String, Resource> resources;
    private final Map<String, Attribute> attributes;

    public AbacAuthModel(List<Policy> policies, Map<String, Resource> resources, Map<String, Attribute> attributes) {
        this.policies = policies;
        this.resources = resources;
        this.attributes = attributes;
    }

    public String getCombiningAlgorithm() {
        return combiningAlgorithm;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public Map<String, Resource> getResources() {
        return resources;
    }

    public Map<String, Attribute> getAttributes() {
        return attributes;
    }
}
