package custis.easyabac.core.model.easy;

import java.util.List;
import java.util.Map;

public class EasyPolicy {
    private String title;
    private List<String> accessToActions;
    private Map<String, EasyRule> rules;
    private List<String> obligations;


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

    public List<String> getObligations() {
        return obligations;
    }

    public void setObligations(List<String> obligations) {
        this.obligations = obligations;
    }
}
