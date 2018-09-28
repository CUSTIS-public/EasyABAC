package custis.easyabac.api.core;

import java.lang.reflect.Method;

public class UnsupportedPermissionCheckerMethodSignature extends RuntimeException {
    public UnsupportedPermissionCheckerMethodSignature(Method method) {
        super("Method " + method.getName() + " not supported");
    }

    public UnsupportedPermissionCheckerMethodSignature(Method method, String message) {
        super("Method " + method.getName() + ": " + message);
    }
}
