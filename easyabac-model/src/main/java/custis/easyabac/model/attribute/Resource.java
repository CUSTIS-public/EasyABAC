package custis.easyabac.model.attribute;

import java.util.List;
import java.util.Set;

public class Resource {
    private final String id;
    private final String title;
    private final Set<String> actions;
    private final List<Attribute> attributes;

    public Resource(String id, String title, Set<String> actions, List<Attribute> attributes) {
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

    public Set<String> getActions() {
        return actions;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
