package ru.custis.easyabac.core.models.policy;

import ru.custis.easyabac.core.models.attribute.Attribute;

public class TargetCondition {
    private Attribute attribute;
    private String attributeValue;
    private Function function;
    private final String expression;

    public TargetCondition(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

}
