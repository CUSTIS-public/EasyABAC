package custis.easyabac.core.model.abac;

import custis.easyabac.core.model.abac.attribute.Attribute;

public class TargetCondition {
    private String id;
    private Attribute firstOperand;
    private String secondOperand;
    private Function function;
    private final String expression;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TargetCondition(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

}
