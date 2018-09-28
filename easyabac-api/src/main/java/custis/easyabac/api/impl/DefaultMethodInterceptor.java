package custis.easyabac.api.impl;

import custis.easyabac.api.utils.Lazy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

public class DefaultMethodInterceptor implements MethodInterceptor {

    private final Map<Method, MethodHandle> methodHandleCache = new ConcurrentReferenceHashMap<>(10, ConcurrentReferenceHashMap.ReferenceType.WEAK);
    private final Lazy<Constructor<MethodHandles.Lookup>> constructor = Lazy.of(DefaultMethodInterceptor::getLookupConstructor);

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Nullable
    @Override
    public Object invoke(@SuppressWarnings("null") MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        if (!method.isDefault()) {
            return invocation.proceed();
        }

        Object[] arguments = invocation.getArguments();
        Object proxy = ((ProxyMethodInvocation) invocation).getProxy();

        return getMethodHandle(method).bindTo(proxy).invokeWithArguments(arguments);
    }

    private MethodHandle getMethodHandle(Method method) throws Exception {

        MethodHandle handle = methodHandleCache.get(method);

        if (handle == null) {

            handle = lookup(method);
            methodHandleCache.put(method, handle);
        }

        return handle;
    }

    private MethodHandle lookup(Method method) throws ReflectiveOperationException {

        if (!isAvailable()) {
            throw new IllegalStateException("Could not obtain MethodHandles.lookup constructor!");
        }

        Constructor<MethodHandles.Lookup> constructor = this.constructor.get();

        return constructor.newInstance(method.getDeclaringClass()).unreflectSpecial(method, method.getDeclaringClass());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor.MethodHandleLookup#isAvailable()
     */
    boolean isAvailable() {
        return constructor.orElse(null) != null;
    }

    private static Constructor<MethodHandles.Lookup> getLookupConstructor() {

        try {

            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
            ReflectionUtils.makeAccessible(constructor);

            return constructor;
        } catch (Exception ex) {

            // this is the signal that we are on Java 9 (encapsulated) and can't use the accessible constructor approach.
            if (ex.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
                return null;
            }

            throw new IllegalStateException(ex);
        }
    }
}
