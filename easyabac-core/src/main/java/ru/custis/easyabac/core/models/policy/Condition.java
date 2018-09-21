package ru.custis.easyabac.core.models.policy;

import ru.custis.easyabac.core.models.attribute.Attribute;

public class Condition {

    private boolean negation;
    private Attribute attribute;
    private Attribute attributeValue;
    private Function function;
    private String expression;

    public Condition(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isNegation() {
        return negation;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Attribute getAttributeValue() {
        return attributeValue;
    }

    public Function getFunction() {
        return function;
    }
}
