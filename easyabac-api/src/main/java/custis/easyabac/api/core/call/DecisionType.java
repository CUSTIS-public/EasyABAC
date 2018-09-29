package custis.easyabac.api.core.call;

import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;
import custis.easyabac.pdp.AuthResponse;

import java.lang.reflect.Method;

public enum DecisionType {
    PERMIT("permit", "permitted", AuthResponse.Decision.PERMIT),
    DENY("deny", "denied", AuthResponse.Decision.DENY),
    NOT_APPLICABLE("notapplicable", AuthResponse.Decision.NOT_APPLICABLE),
    INDETERMINATE("indeterminate", AuthResponse.Decision.INDETERMINATE);

    private final String code;
    private final String secondForm;
    private final AuthResponse.Decision decision;

    DecisionType(String code, AuthResponse.Decision decision) {
        this(code, code, decision);
    }

    DecisionType(String code, String secondForm, AuthResponse.Decision decision) {
        this.code = code;
        this.secondForm = secondForm;
        this.decision = decision;
    }

    public String getCode() {
        return code;
    }

    public String getSecondForm() {
        return secondForm;
    }

    public AuthResponse.Decision getDecision() {
        return decision;
    }

    public static DecisionType findByMethod(Method method, MethodType methodType) {
        String methodName = method.getName().toLowerCase().substring(methodType.getCode().length());
        for (DecisionType value : values()) {
            if (methodName.startsWith(value.getSecondForm())) {
                return value;
            }
        }
        throw new UnsupportedPermissionCheckerMethodSignature(method, "Unknown Decision in method signature");
    }
}
