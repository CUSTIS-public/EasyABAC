package custis.easyabac.api.core;

import java.lang.reflect.Method;

public class UnsupportedDynamicMethodSignature extends RuntimeException {
    public UnsupportedDynamicMethodSignature(Method method) {
        super("Method " + method.toString() + " not supported");
    }

    public UnsupportedDynamicMethodSignature(Method method, String message) {
        super("Method " + method.toString() + ": " + message);
    }
}
