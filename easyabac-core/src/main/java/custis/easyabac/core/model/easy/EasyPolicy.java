package custis.easyabac.core.model.easy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EasyPolicy {
    private String title;
    private List<String> accessToActions = Collections.emptyList();
    private Map<String, EasyRule> rules = Collections.emptyMap();
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

    public Map<String, EasyRule> getRules() {
        return rules;
    }

    public void setRules(Map<String, EasyRule> rules) {
        this.rules = rules;
    }

    public List<String> getReturnAttributes() {
        return returnAttributes;
    }

    public void setReturnAttributes(List<String> returnAttributes) {
        this.returnAttributes = returnAttributes;
    }
}
