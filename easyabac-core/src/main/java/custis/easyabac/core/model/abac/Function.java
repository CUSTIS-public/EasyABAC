package custis.easyabac.core.model.abac;

import java.util.Optional;
import java.util.stream.Stream;

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

    public static Optional<Function> of(String name) {
        return Stream.of(Function.values())
                .filter(v -> v.functionName.equals(name))
                .findFirst();
    }
}