package custis.easyabac.api.core.call.callprocessor;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.UnsupportedDynamicMethodSignature;
import custis.easyabac.api.core.call.GettingReturnType;
import custis.easyabac.api.core.call.MethodType;
import custis.easyabac.api.core.call.converters.GettingResultConverter;
import custis.easyabac.api.core.call.converters.ResultConverter;
import custis.easyabac.api.core.call.getters.RequestGenerator;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;

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
            throw new UnsupportedDynamicMethodSignature(method, "ClassNotFound " + e.getMessage());
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
        Class<?> returnType = method.getReturnType();
        if (List.class.equals(returnType)) {
            // must not have multiple params
            boolean foundNotMultiple = false;
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (checkerInfo.getResourceType().isAssignableFrom(parameterType) || checkerInfo.getActionType().isAssignableFrom(parameterType)) {
                    foundNotMultiple = true;
                    break;
                }
            }
            if (!foundNotMultiple) {
                throw new UnsupportedDynamicMethodSignature(method, "List<> for get* methods requires not multiple resource or action");
            }
        }
        if (Map.class.equals(returnType)) {

        }
    }


    private void checkReturnType() throws ClassNotFoundException {
        Class<?> returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        if (!(genericReturnType instanceof ParameterizedType)) {
            throw new UnsupportedDynamicMethodSignature(method, "List<> or Map<> required for get* methods as return");
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;

        if (methodType != MethodType.GET) {
            return;
        }

        if (List.class.equals(returnType)) {
            returningList = true;
            // first parameter is not list or map
            if (gettingReturnType == GettingReturnType.RESOURCES) {
                // returning resources
                checkList(parameterizedType, checkerInfo.getResourceType());
            } else {
                // returning actions
                checkList(parameterizedType, checkerInfo.getActionType());
            }
        } else if (Map.class.equals(returnType)) {
            returningList = false;
            if (gettingReturnType == GettingReturnType.RESOURCES) {
                // returning objects
                // should be Map<Action, List<Resource>>
                checkMap(parameterizedType, checkerInfo.getActionType(), checkerInfo.getResourceType());
            } else {
                // returning actions
                // should be Map<Resource, List<Action>>
                checkMap(parameterizedType, checkerInfo.getResourceType(), checkerInfo.getActionType());
            }
        } else {
            throw new UnsupportedDynamicMethodSignature(method, "List<> or Map<> required for get* methods as return");
        }
    }

    private void checkList(ParameterizedType parameterizedType, Class<?> value) throws ClassNotFoundException {
        Class<?> genericClass = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName());

        if (!value.isAssignableFrom(genericClass)) {
            throw new UnsupportedDynamicMethodSignature(method, "List<" + value.getSimpleName() + "> required for get* methods");
        }
    }

    private void checkMap(ParameterizedType parameterizedType, Class<?> key, Class<?> listValue) throws ClassNotFoundException {
        Type[] types = parameterizedType.getActualTypeArguments();
        Type keyType = types[0];
        Type listValueType = types[1];

        if (!key.isAssignableFrom(Class.forName(keyType.getTypeName()))) {
            throw new UnsupportedDynamicMethodSignature(method, "Map<" + key.getSimpleName() + ", List<" + listValue.getSimpleName() + ">> required for get* methods as return");
        }

        if (listValueType instanceof ParameterizedType) {
            ParameterizedType listGeneric = (ParameterizedType) listValueType;
            if (!List.class.isAssignableFrom(Class.forName(listGeneric.getRawType().getTypeName()))) {
                throw new UnsupportedDynamicMethodSignature(method, "Map<" + key.getSimpleName() + ", List<" + listValue.getSimpleName() + ">> required for get* methods as return");
            }
        } else {
            throw new UnsupportedDynamicMethodSignature(method, "Map<" + key.getSimpleName() + ", List<" + listValue.getSimpleName() + ">> required for get* methods as return");
        }
    }
}
