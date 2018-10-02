package custis.easyabac.core.model.abac;

import custis.easyabac.core.model.abac.attribute.Attribute;

import java.util.List;

public class Condition {
    private final String id;
    private final boolean negation;
    private final Attribute firstOperand;
    private final Attribute secondOperandAttribute;
    private final String secondOperandValue;
    private final List<String> secondOperandArray;
    private final Function function;

    public Condition(String id, boolean negation, Attribute firstOperand, Attribute secondOperandAttribute,
                     String secondOperandValue, List<String> secondOperandArray, Function function) {
        this.id = id;
        this.negation = negation;
        this.firstOperand = firstOperand;
        this.secondOperandAttribute = secondOperandAttribute;
        this.secondOperandValue = secondOperandValue;
        this.secondOperandArray = secondOperandArray;
        this.function = function;
    }

    public String getId() {
        return id;
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
