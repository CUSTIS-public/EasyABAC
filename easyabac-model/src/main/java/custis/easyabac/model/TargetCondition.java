package custis.easyabac.model;

import custis.easyabac.model.attribute.Attribute;

public class TargetCondition {
    private String id;
    private Attribute firstOperand;
    private String secondOperand;
    private Function function;
    private String expression;

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

}
