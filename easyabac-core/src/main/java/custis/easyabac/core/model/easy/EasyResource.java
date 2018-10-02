package custis.easyabac.core.model.easy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EasyResource {
    private String title;
    private Set<String> actions = Collections.emptySet();
    private List<EasyAttribute> attributes = Collections.emptyList();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    public List<EasyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<EasyAttribute> attributes) {
        this.attributes = attributes;
    }
}
