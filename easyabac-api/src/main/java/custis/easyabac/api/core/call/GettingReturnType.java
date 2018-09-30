package custis.easyabac.api.core.call;

import java.lang.reflect.Method;

public enum GettingReturnType {

    RESOURCES("resources"), ACTIONS("actions");

    private final String code;

    GettingReturnType() {
        this(null);
    }

    GettingReturnType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GettingReturnType findByMethod(Method method, MethodType methodType, DecisionType decisionType) {
        String methodName = method.getName().toLowerCase();
        int lastLine = methodName.lastIndexOf("_"); // на случай нескольких методов из-за erasure
        if (lastLine != -1) {
            methodName = methodName.substring(0, lastLine);
        }
        methodName = methodName.substring(methodType.getCode().length() + decisionType.getSecondForm().length());
        for (GettingReturnType value : values()) {
            if (value.getCode() != null && methodName.equals(value.getCode())) {
                return value;
            }
        }
        throw new IllegalArgumentException(methodName);
    }
}
