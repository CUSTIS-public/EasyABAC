package custis.easyabac.core.model.policy;

public enum Function {
    EQUAL("=="),
    GREATER(">"),
    LESS("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<="),
    IN("in"),
    ONE_OF("one-of"),
    SUBSET("subset");

    private String functionName;

    Function(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}
