package custis.easyabac.core.trace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Policy with trace
 */
public class CalculatedPolicy extends AbstractCalculatedPolicy {


    private List<CalculatedRule> rules = new ArrayList<>();

    public CalculatedPolicy(String id) {
       super(id);
    }

    public void addRule(CalculatedRule calculatedRule) {
        rules.add(calculatedRule);
    }


}
