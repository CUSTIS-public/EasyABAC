package custis.easyabac.core.model.policy;

import custis.easyabac.core.model.attribute.Attribute;

public class TargetCondition {
    private Attribute firstOperand;
    private String secondOperand;
    private Function function;
    private String expression;

    public TargetCondition(String expression) {
        this.expression = expression;
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