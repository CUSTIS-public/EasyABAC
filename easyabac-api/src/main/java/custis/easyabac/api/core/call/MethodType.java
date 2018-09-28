package custis.easyabac.api.core.call;

import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;

import java.lang.reflect.Method;

public enum MethodType {
    ENSURE("ensure"), IS("is"), GET("get");

    private final String code;

    MethodType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MethodType findByMethod(Method method) {
        String methodName = method.getName().toLowerCase();
        for (MethodType value : values()) {
            if (methodName.startsWith(value.getCode())) {
                return value;
            }
        }
        throw new UnsupportedPermissionCheckerMethodSignature(method);
    }


}
