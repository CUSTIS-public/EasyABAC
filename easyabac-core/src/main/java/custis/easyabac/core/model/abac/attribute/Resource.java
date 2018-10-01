package custis.easyabac.core.model.abac.attribute;

import java.util.List;

public class Resource {
    private final String id;
    private final String title;
    private final List<String> actions;
    private final List<Attribute> attributes;

    public Resource(String id, String title, List<String> actions, List<Attribute> attributes) {
        this.id = id;
        this.title = title;
        this.actions = actions;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
