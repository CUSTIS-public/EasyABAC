package custis.easyabac.api.core.call;

import java.lang.reflect.Method;

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
        if (methodName.split("or").length > 1) {
            return ANY;
        } else {
            return ALL;
        }
    }
}
