package custis.easyabac.core.model.easy;

import java.util.List;
import java.util.Map;

public class EasyAuthModel {
    private Map<String, EasyObject> resources;
    private List<EasyPolicy> permissions;

    public Map<String, EasyObject> getResources() {
        return resources;
    }

    public void setResources(Map<String, EasyObject> resources) {
        this.resources = resources;
    }

    public List<EasyPolicy> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<EasyPolicy> permissions) {
        this.permissions = permissions;
    }
}
