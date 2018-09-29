package custis.easyabac.api.core.call.callprocessor;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;
import custis.easyabac.api.core.call.GettingReturnType;
import custis.easyabac.api.core.call.MethodType;
import custis.easyabac.api.core.call.converters.GettingResultConverter;
import custis.easyabac.api.core.call.converters.ResultConverter;
import custis.easyabac.api.core.call.getters.RequestGenerator;
import custis.easyabac.pdp.AttributiveAuthorizationService;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Call Processor for dynamic methods
 */
public class GettingMethodCallProcessor extends MethodCallProcessor {

    private final GettingReturnType gettingReturnType;
    private boolean returningList;

    public GettingMethodCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        super(permissionCheckerInformation, method, attributiveAuthorizationService);
        this.gettingReturnType = GettingReturnType.findByMethod(method, methodType, decisionType);

        // method return type
        try {
            checkReturnType();
            checkParameters();
        } catch (ClassNotFoundException e) {
            throw new UnsupportedPermissionCheckerMethodSignature(method, "ClassNotFound " + e.getMessage());
        }
    }

    @Override
    protected ResultConverter prepareResultConverter() {
        return new GettingResultConverter(decisionType, gettingReturnType, returningList, checkerInfo);
    }


    @Override
    protected Optional<RequestGenerator> prepareCustomAttributesValuesGetter() {
        return Optional.empty();
    }

    private void checkParameters() {
        // check generic types or List or Map
    }


    private void checkReturnType() throws ClassNotFoundException {
        Class<?> returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        if (!(genericReturnType instanceof ParameterizedType)) {
            throw new UnsupportedPermissionCheckerMethodSignature(method, "List<> or Map<> required for get* methods as return");
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;

        if (methodType != MethodType.GET) {
            return;
        }

        if (List.class.equals(returnType)) {
            returningList = true;
            // first parameter is not list or map
            Class<?> firstClass = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName());

            if (gettingReturnType == GettingReturnType.RESOURCES) {
                // returning objects
               if (!checkerInfo.getResourceType().isAssignableFrom(firstClass)) {
                   throw new UnsupportedPermissionCheckerMethodSignature(method, "List<" + checkerInfo.getResourceType() + "> required for get*Resources methods");
               }
            } else {
                // returning actions
                if (!checkerInfo.getActionType().isAssignableFrom(firstClass)) {
                    throw new UnsupportedPermissionCheckerMethodSignature(method, "List<" + checkerInfo.getActionType() + "> required for get*Resources methods");
                }
            }
        } else if (Map.class.equals(returnType)) {
            returningList = false;
            if (gettingReturnType == GettingReturnType.RESOURCES) {
                // returning objects

            } else {
                // returning actions
            }
        } else {
            throw new UnsupportedPermissionCheckerMethodSignature(method, "List<> or Map<> required for get* methods as return");
        }
    }
}
