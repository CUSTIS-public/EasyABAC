package custis.easyabac.core.model.easy;

import java.util.List;

public class EasyResource {
    private String id;
    private String title;
    private List<String> actions;
    private List<EasyAttribute> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<EasyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<EasyAttribute> attributes) {
        this.attributes = attributes;
    }

}
