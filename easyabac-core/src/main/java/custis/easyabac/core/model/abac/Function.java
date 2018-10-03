package custis.easyabac.core.model.abac;

import java.util.Optional;
import java.util.stream.Stream;

import custis.easyabac.core.init.EasyAbacInitException;

public enum Function {
    EQUAL("=="),
    GREATER(">"),
    LESS("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<="),
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
        throw new EasyAbacInitException("Type " + functionName + " not supported");

}

    public static Optional<Function> of(String name) {
        return Stream.of(Function.values())
                .filter(v -> v.functionName.equals(name))
                .findFirst();
    }
}