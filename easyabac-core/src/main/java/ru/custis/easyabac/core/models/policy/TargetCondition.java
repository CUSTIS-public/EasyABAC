package ru.custis.easyabac.core.models.policy;

import ru.custis.easyabac.core.models.attribute.Attribute;

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
