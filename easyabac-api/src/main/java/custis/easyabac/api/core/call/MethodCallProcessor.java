package custis.easyabac.api.core.call;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;

import java.lang.reflect.Method;
import java.util.List;

public abstract class MethodCallProcessor {
    protected final AttributiveAuthorizationService attributiveAuthorizationService;
    protected final Method method;
    protected final PermissionCheckerInformation permissionCheckerInformation;
    protected final MethodType methodType;
    protected final DecisionType decisionType;

    protected MethodCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        this.permissionCheckerInformation = permissionCheckerInformation;
        this.method = method;
        this.attributiveAuthorizationService = attributiveAuthorizationService;
        this.methodType = MethodType.findByMethod(method);
        this.decisionType = DecisionType.findByMethod(method, methodType);
    }


    public Object execute(Object[] arguments) {
        List<List<AuthAttribute>> req = generateAttributeRequest(arguments);
        List<AuthResponse> responses = attributiveAuthorizationService.authorizeMultiple(req);
        return convertResponse(responses);
    }

    protected abstract Object convertResponse(List<AuthResponse> responses);

    protected abstract List<List<AuthAttribute>> generateAttributeRequest(Object[] arguments);
}
