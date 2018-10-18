package custis.easyabac.model;

import custis.easyabac.model.attribute.Attribute;

import java.util.Collections;
import java.util.List;

public class Condition {
    private final String id;
    private final boolean negation;
    private final Attribute firstOperand;
    private final Attribute secondOperandAttribute;
    private final List<String> secondOperandValue;
    private final Function function;

    public Condition(String id, boolean negation, Attribute firstOperand, Attribute secondOperandAttribute,
                     Function function) {
        this.id = id;
        this.negation = negation;
        this.firstOperand = firstOperand;
        this.secondOperandAttribute = secondOperandAttribute;
        this.secondOperandValue = Collections.emptyList();
        this.function = function;
    }

    public Condition(String id, boolean negation, Attribute firstOperand, List<String> secondOperandValue, Function function) {
        this.id = id;
        this.negation = negation;
        this.firstOperand = firstOperand;
        this.secondOperandAttribute = null;
        this.secondOperandValue = secondOperandValue;
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

    public List<String> getSecondOperandValue() {
        return secondOperandValue;
    }

    public Function getFunction() {
        return function;
    }
}
