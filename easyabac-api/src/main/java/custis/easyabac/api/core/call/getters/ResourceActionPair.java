package custis.easyabac.api.core.call.getters;

public class ResourceActionPair {

    private Object resource;
    private Object action;

    public ResourceActionPair(Object resource, Object action) {
        this.resource = resource;
        this.action = action;
    }

    public Object getResource() {
        return resource;
    }

    public Object getAction() {
        return action;
    }

    public static ResourceActionPair of(Object resource, Object action) {
        return new ResourceActionPair(resource, action);
    }
}
