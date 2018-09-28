package custis.easyabac.api.core.call;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Call Processor for dynamic methods
 */
public class GettingMethodCallProcessor extends MethodCallProcessor {


    public GettingMethodCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        super(permissionCheckerInformation, method, attributiveAuthorizationService);

        prepareGetters();

        // method return type
    }

    @Override
    protected List<List<AuthAttribute>> generateAttributeRequest(Object[] arguments) {
        return Collections.emptyList();
    }

    @Override
    protected Object convertResponse(List<AuthResponse> responses) {
        return Void.class;
    }

    private void prepareGetters() {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0){
            throw new UnsupportedPermissionCheckerMethodSignature(method, "Method require parameters!");
        }


    }

}
