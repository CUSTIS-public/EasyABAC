package custis.easyabac.core.model.abac;

import custis.easyabac.core.model.abac.attribute.Attribute;

public class TargetCondition {
    private Attribute firstOperand;
    private String secondOperand;
    private Function function;
    private final String expression;

    public TargetCondition(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

}
