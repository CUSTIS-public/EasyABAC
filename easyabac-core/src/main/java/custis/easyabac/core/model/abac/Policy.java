package custis.easyabac.core.model.abac;

import java.util.Map;

public class Policy {
    private String title;
    private String combiningAlgorithm= "deny-unless-permit";;
    private Target target;
    private Map<String, Rule> rules;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCombiningAlgorithm() {
        return combiningAlgorithm;
    }

    public void setCombiningAlgorithm(String combiningAlgorithm) {
        this.combiningAlgorithm = combiningAlgorithm;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    public void setRules(Map<String, Rule> rules) {
        this.rules = rules;
    }
}
