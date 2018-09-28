package custis.easyabac.api.core;

import java.lang.reflect.Method;

public class UnsupportedPermissionCheckerMethodSignature extends RuntimeException {
    public UnsupportedPermissionCheckerMethodSignature(Method method) {
        super("Method " + method.toString() + " not supported");
    }

    public UnsupportedPermissionCheckerMethodSignature(Method method, String message) {
        super("Method " + method.toString() + ": " + message);
    }
}
