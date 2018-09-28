package custis.easyabac.core.model.attribute.load;

import java.util.List;

public class EasyObject {
    private String title;
    private List<EasyAttribute> actions;
    private List<EasyAttribute> attributes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<EasyAttribute> getActions() {
        return actions;
    }

    public void setActions(List<EasyAttribute> actions) {
        this.actions = actions;
    }

    public List<EasyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<EasyAttribute> attributes) {
        this.attributes = attributes;
    }
}
