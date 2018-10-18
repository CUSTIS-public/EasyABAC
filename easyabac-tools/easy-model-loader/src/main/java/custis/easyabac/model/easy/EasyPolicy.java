package custis.easyabac.model.easy;

import java.util.Collections;
import java.util.List;

public class EasyPolicy {
    private String title;
    private List<String> accessToActions = Collections.emptyList();
    private List<EasyRule> rules = Collections.emptyList();
    private List<String> returnAttributes = Collections.emptyList();


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAccessToActions() {
        return accessToActions;
    }

    public void setAccessToActions(List<String> accessToActions) {
        this.accessToActions = accessToActions;
    }

    public List<EasyRule> getRules() {
        return rules;
    }

    public void setRules(List<EasyRule> rules) {
        this.rules = rules;
    }

    public List<String> getReturnAttributes() {
        return returnAttributes;
    }

    public void setReturnAttributes(List<String> returnAttributes) {
        this.returnAttributes = returnAttributes;
    }
}
