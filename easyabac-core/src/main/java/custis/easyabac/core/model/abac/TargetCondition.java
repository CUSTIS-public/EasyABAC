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

    public TargetCondition(String id, Function function, Attribute firstOperand, String secondOperand) {
        this.id = id;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.function = function;
    }

    public TargetCondition(Attribute firstOperand, String secondOperand, Function function) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.function = function;
    }

    public Attribute getFirstOperand() {
        return firstOperand;
    }

    public String getSecondOperand() {
        return secondOperand;
    }

    public Function getFunction() {
        return function;
    }

    public String getExpression() {
        return expression;
    }
}
