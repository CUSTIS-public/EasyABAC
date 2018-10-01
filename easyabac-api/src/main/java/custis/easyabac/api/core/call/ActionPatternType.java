package custis.easyabac.api.core.call;

import java.lang.reflect.Method;

import static custis.easyabac.api.core.call.Constants.LEXEM_OR;

public enum ActionPatternType {

    EMPTY, ANY("any"), ALL("all");

    private final String code;

    ActionPatternType() {
        this(null);
    }

    ActionPatternType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ActionPatternType findByMethod(Method method, MethodType methodType, DecisionType decisionType) {
        String methodName = method.getName().toLowerCase();
        int lastLine = methodName.lastIndexOf("_"); // на случай нескольких методов из-за erasure
        if (lastLine != -1) {
            methodName = methodName.substring(0, lastLine);
        }
        methodName = methodName.substring(methodType.getCode().length() + decisionType.getSecondForm().length());
        if (methodName.isEmpty()) {
            return EMPTY;
        }
        for (ActionPatternType value : values()) {
            if (value.getCode() != null && methodName.equals(value.getCode())) {
                return value;
            }
        }

        // possible concrete methods
        if (methodName.split(LEXEM_OR).length > 1) {
            return ANY;
        } else {
            return ALL;
        }
    }
}
