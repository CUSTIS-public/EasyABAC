package custis.easyabac.core.model.abac;

import custis.easyabac.core.model.abac.attribute.Attribute;

import java.util.List;

public class Condition {
    private String id;
    private boolean negation;
    private Attribute firstOperand;
    private Attribute secondOperandAttribute;
    private String secondOperandValue;
    private List<String> secondOperandArray;
    private Function function;
    private String expression;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
