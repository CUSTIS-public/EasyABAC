package custis.easyabac.core.model.policy;

import custis.easyabac.core.model.attribute.Attribute;

import java.util.List;

public class Condition {

    private boolean negation;
    private Attribute firstOperand;
    private Attribute secondOperandAttribute;
    private String secondOperandValue;
    private List<String> secondOperandArray;
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

    public Attribute getFirstOperand() {
        return firstOperand;
    }

    public Attribute getSecondOperandAttribute() {
        return secondOperandAttribute;
    }

    public String getSecondOperandValue() {
        return secondOperandValue;
    }

    public List<String> getSecondOperandArray() {
        return secondOperandArray;
    }

    public Function getFunction() {
        return function;
    }
}
