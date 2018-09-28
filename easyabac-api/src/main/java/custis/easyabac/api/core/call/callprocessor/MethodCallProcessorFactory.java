package custis.easyabac.api.core.call.callprocessor;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.call.MethodType;
import custis.easyabac.pdp.AttributiveAuthorizationService;

import java.lang.reflect.Method;

/**
 * Call Processor for dynamic methods
 */
public class MethodCallProcessorFactory {


    public static MethodCallProcessor createCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        MethodType methodType = MethodType.findByMethod(method);
        MethodCallProcessor callProcessor;
        switch (methodType) {
            case ENSURE:
            case IS:
                 callProcessor = new CheckingMethodCallProcessor(permissionCheckerInformation, method, attributiveAuthorizationService);
                 break;
            case GET:
                 callProcessor = new GettingMethodCallProcessor(permissionCheckerInformation, method, attributiveAuthorizationService);
                 break;
            default:
                throw new UnsupportedOperationException("not implemented");
        }
        callProcessor.afterPropertiesSet();
        return callProcessor;
    }
}
