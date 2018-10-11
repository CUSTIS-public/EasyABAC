package custis.easyabac.core.trace.model;

import org.wso2.balana.Rule;

/**
 * Policy with trace
 */
public class CalculatedRule {

    private final String id;
    private final CalculatedResult result;


    public CalculatedRule(Rule rule, String realResult) {
        this.id = rule.getId().toString();
        this.result = new CalculatedResult(realResult);
    }
}
