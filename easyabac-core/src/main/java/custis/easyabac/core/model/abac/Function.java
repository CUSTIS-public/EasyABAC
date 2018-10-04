package custis.easyabac.core.model.abac;

import custis.easyabac.core.init.EasyAbacInitException;

public enum Function {
    EQUAL("=="),
    GREATER(">"),
    LESS("<"),
    GREATER_OR_EQUEL(">="),
    LESS_OR_EQUEL("<="),
    IN("in"),
    ONE_OF("one-of"),
    SUBSET("subset");
    private String easyName;

    Function(String easyName) {
        this.easyName = easyName;
    }

    public String getEasyName() {
        return easyName;
    }

    public static Function findByEasyName(String functionName) throws EasyAbacInitException {
        for (Function value : Function.values()) {
            if (functionName.equals(value.getEasyName())) {
                return value;
            }
        }
        throw new EasyAbacInitException("Function " + functionName + " is not supported");

    }
}
