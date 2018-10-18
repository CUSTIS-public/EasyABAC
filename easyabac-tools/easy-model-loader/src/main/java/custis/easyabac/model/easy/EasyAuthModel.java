package custis.easyabac.model.easy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EasyAuthModel {
    private Map<String, EasyResource> resources = Collections.emptyMap();
    private List<EasyPolicy> permissions = Collections.emptyList();

    public Map<String, EasyResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, EasyResource> resources) {
        this.resources = resources;
    }

    public List<EasyPolicy> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<EasyPolicy> permissions) {
        this.permissions = permissions;
    }
}
