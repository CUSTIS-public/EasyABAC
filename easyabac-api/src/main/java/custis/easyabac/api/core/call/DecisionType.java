package custis.easyabac.api.core.call;

import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;
import custis.easyabac.pdp.AuthResponse;

import java.lang.reflect.Method;

public enum DecisionType {
    PERMIT("permit", "permitted", AuthResponse.AuthResult.PERMIT),
    DENY("deny", "denied", AuthResponse.AuthResult.DENY),
    NOT_APPLICABLE("notapplicable", AuthResponse.AuthResult.NOT_APPLICABLE),
    INDETERMINATE("indeterminate", AuthResponse.AuthResult.INDETERMINATE);

    private final String code;
    private final String secondForm;
    private final AuthResponse.AuthResult authResult;

    DecisionType(String code, AuthResponse.AuthResult authResult) {
        this(code, code, authResult);
    }

    DecisionType(String code, String secondForm, AuthResponse.AuthResult authResult) {
        this.code = code;
        this.secondForm = secondForm;
        this.authResult = authResult;
    }

    public String getCode() {
        return code;
    }

    public String getSecondForm() {
        return secondForm;
    }

    public AuthResponse.AuthResult getAuthResult() {
        return authResult;
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
