package custis.easyabac.api.impl;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.call.AttributiveMethodCallFactory;
import custis.easyabac.api.core.call.MethodCallProcessor;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Method Interceptor for dynamic methods like ensure*
 */
public class DynamicMethodInterceptor implements MethodInterceptor {

    private final Map<Method, MethodCallProcessor> callProcessors = new HashMap<>();
    private final PermissionCheckerInformation permissionCheckerInformation;
    private final AttributiveAuthorizationService attributiveAuthorizationService;

    public DynamicMethodInterceptor(PermissionCheckerInformation permissionCheckerInformation, AttributiveAuthorizationService attributiveAuthorizationService) {
        this.permissionCheckerInformation = permissionCheckerInformation;
        this.attributiveAuthorizationService = attributiveAuthorizationService;
        lookupMethods(permissionCheckerInformation);
    }

    private void lookupMethods(PermissionCheckerInformation permissionCheckerInformation) {
        if (permissionCheckerInformation.hasAuthorizationCalls()) {
            for (Method authorizationCall : permissionCheckerInformation.getAuthorizationCalls()) {
                callProcessors.put(authorizationCall, AttributiveMethodCallFactory.createCallProcessor(permissionCheckerInformation, authorizationCall, attributiveAuthorizationService));
            }
        }

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();

        if (hasCustomCall(method)) {
            return callProcessors.get(method).execute(arguments);
        }

        return invocation.proceed();
    }

    private boolean hasCustomCall(Method method) {
        return callProcessors.containsKey(method);
    }
}
