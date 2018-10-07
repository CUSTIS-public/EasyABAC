package custis.easyabac.core.model.abac;

import custis.easyabac.core.model.abac.attribute.Attribute;

public class TargetCondition {
    private final String id;
    private final Function function;
    private final Attribute firstOperand;
    private final String secondOperand;


    public TargetCondition(String id, Function function, Attribute firstOperand, String secondOperand) {
        this.id = id;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.function = function;
    }

    public String getId() {
        return id;
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
