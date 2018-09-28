package custis.easyabac.api.core.call;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;
import custis.easyabac.api.impl.AttributeValueExtractor;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static custis.easyabac.api.core.call.Constants.LEXEM_AND;
import static custis.easyabac.api.core.call.Constants.LEXEM_OR;

/**
 * Call Processor for dynamic methods
 */
public class CheckingMethodCallProcessor extends MethodCallProcessor {

    private final ActionPatternType actionPatternType;

    private AttributesValuesGetter attributesValuesGetter;
    private ResultConverter converter;

    public CheckingMethodCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        super(permissionCheckerInformation, method, attributiveAuthorizationService);
        this.actionPatternType = ActionPatternType.findByMethod(method, methodType, decisionType);
        this.converter = new CheckingResultConverter(methodType, decisionType, actionPatternType);
        this.attributesValuesGetter = prepareGetter();

        // method return type
        checkReturnType();
        checkExceptions();
    }

    @Override
    protected Map<RequestId, List<AuthAttribute>> generateAttributeRequest(Object[] arguments) {
        return attributesValuesGetter.getAttributes(arguments);
    }

    @Override
    protected Object convertResponse(Map<RequestId, AuthResponse> responses) {
        return converter.convert(responses);
    }

    private AttributesValuesGetter prepareGetter() {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0){
            throw new UnsupportedPermissionCheckerMethodSignature(method, "Method require parameters!");
        }

        Class<?> first = parameterTypes[0];
        if (permissionCheckerInformation.getResourceType().isAssignableFrom(first)) {
            // first parameter is Resource
            if (parameterTypes.length == 1) {
                int methodStartIndex = methodType.getCode().length() + decisionType.getSecondForm().length();
                String actionsString = method.getName().toLowerCase().substring(methodStartIndex,
                        method.getName().length() - actionPatternType.getCode().length()
                );

                String[] splittedActions = actionsString.toLowerCase().split(LEXEM_OR);
                if (splittedActions.length != 1) {
                    return new ResourceWithListOfActions(Arrays.asList(splittedActions));
                } else {
                    splittedActions = actionsString.toLowerCase().split(LEXEM_AND);
                    return new ResourceWithListOfActions(Arrays.asList(splittedActions));
                }


            } else {
                Class<?> second = parameterTypes[1];
                if (permissionCheckerInformation.getActionType().isAssignableFrom(second)) {
                    return new SingleResourceAndAction();
                } else {
                    if (List.class.isAssignableFrom(second)) {
                        return new ResourceWithListOfActions();
                    } else {
                        throw new UnsupportedPermissionCheckerMethodSignature(method, "Unexpected type " + second);
                    }
                }
            }


        } else {
           if (Map.class.isAssignableFrom(first)) {
               return new MapAttributeValueGetter();
           } else {
               throw new UnsupportedPermissionCheckerMethodSignature(method, "Unexpected type " + first);
           }
        }

    }

    private void checkExceptions() {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (methodType == MethodType.ENSURE) {
            for (Class<?> exceptionType : exceptionTypes) {
                if (exceptionType.equals(NotExpectedResultException.class)) {
                    return;
                }
            }
            throw new UnsupportedPermissionCheckerMethodSignature(method, "NotExpectedResultException required for ensure* methods");
        }
    }

    private void checkReturnType() {
        Class<?> returnType = method.getReturnType();
        if (methodType == MethodType.ENSURE) {
            if (!void.class.equals(returnType)) {
                throw new UnsupportedPermissionCheckerMethodSignature(method, "void required for ensure* methods");
            }
            // ok
        } else if (methodType == MethodType.IS) {
            if (!boolean.class.equals(returnType) || Boolean.class.equals(returnType)) {
                throw new UnsupportedPermissionCheckerMethodSignature(method, "boolean required for is* methods");
            }
        }
    }


    private interface AttributesValuesGetter {
        Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects);
    }

    private class SingleResourceAndAction implements AttributesValuesGetter {

        @Override
        public Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects) {
            return new HashMap<RequestId, List<AuthAttribute>>() {
                {
                    put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(objects[0], objects[1]));
                }
            };
        }
    }

    private class ResourceWithListOfActions implements AttributesValuesGetter {

        private List<String> actions = Collections.emptyList();

        public ResourceWithListOfActions() {
        }

        public ResourceWithListOfActions(List<String> actions) {
            this.actions = actions;
        }

        public Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects) {
            Object object = actions.isEmpty() ? objects[1] : actions;
            return (Map<RequestId, List<AuthAttribute>>) ((List) object).stream()
                    .map(o -> AttributeValueExtractor.collectAttributes(objects[0], o))
                    .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));
        }
    }

    private class MapAttributeValueGetter implements AttributesValuesGetter {

        @Override
        public Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects) {
            Object object = objects[0];
            Map<RequestId, List<AuthAttribute>> attributes = new HashMap<>();
            for (Object ob : ((Map) object).entrySet()) {
                Map.Entry castedObj = (Map.Entry) ob;
                attributes.put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(castedObj.getKey(), castedObj.getValue()));
            }
            return attributes;
        }
    }

}
