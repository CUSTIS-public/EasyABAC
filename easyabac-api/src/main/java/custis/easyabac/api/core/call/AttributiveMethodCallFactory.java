package custis.easyabac.api.core.call;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.pdp.AttributiveAuthorizationService;

import java.lang.reflect.Method;

/**
 * Call Processor for dynamic methods
 */
public class AttributiveMethodCallFactory {


    public static MethodCallProcessor createCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        MethodType methodType = MethodType.findByMethod(method);
        switch (methodType) {
            case ENSURE:
            case IS:
                return new CheckingMethodCallProcessor(permissionCheckerInformation, method, attributiveAuthorizationService);
        }
        throw new UnsupportedOperationException("not implemented");
    }
}
