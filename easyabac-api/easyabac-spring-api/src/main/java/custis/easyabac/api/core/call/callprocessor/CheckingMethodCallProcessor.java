package custis.easyabac.api.core.call.callprocessor;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.UnsupportedDynamicMethodSignature;
import custis.easyabac.api.core.call.ActionPatternType;
import custis.easyabac.api.core.call.MethodType;
import custis.easyabac.api.core.call.converters.CheckingResultConverter;
import custis.easyabac.api.core.call.converters.ResultConverter;
import custis.easyabac.api.core.call.getters.RequestGenerator;
import custis.easyabac.api.core.call.getters.TwoArgumentsRequestGenerator;
import custis.easyabac.pdp.AttributiveAuthorizationService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static custis.easyabac.api.core.call.ActionPatternType.*;
import static custis.easyabac.api.core.call.Constants.LEXEM_AND;
import static custis.easyabac.api.core.call.Constants.LEXEM_OR;

/**
 * Call Processor for dynamic methods
 */
public class CheckingMethodCallProcessor extends MethodCallProcessor {

    private final ActionPatternType actionPatternType;

    public CheckingMethodCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        super(permissionCheckerInformation, method, attributiveAuthorizationService);
        this.actionPatternType = ActionPatternType.findByMethod(method, methodType, decisionType);

        // method return type
        checkReturnType();
        checkExceptions();
        checkParameters();
    }

    @Override
    protected ResultConverter prepareResultConverter() {
        return new CheckingResultConverter(methodType, decisionType, actionPatternType);
    }

    @Override
    protected Optional<RequestGenerator> prepareCustomAttributesValuesGetter() {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1) {
            Class<?> first = parameterTypes[0];
            if (checkerInfo.getResourceType().isAssignableFrom(first)) {
                int methodStartIndex = methodType.getCode().length() + decisionType.getSecondForm().length();
                String lowerMethodName = method.getName().toLowerCase();
                int trimEnding = (lowerMethodName.endsWith(ALL.getCode()) || lowerMethodName.endsWith(ANY.getCode())) ? actionPatternType.getCode().length() : 0;
                String actionsString = lowerMethodName.substring(methodStartIndex,
                        lowerMethodName.length() - trimEnding
                );

                String[] splittedActions = actionsString.toLowerCase().split(LEXEM_OR);
                List<Object> typedActions = new ArrayList<>();
                if (splittedActions.length == 1) {
                    splittedActions = actionsString.toLowerCase().split(LEXEM_AND);
                }
                for (String splittedAction : splittedActions) {
                    try {
                        Method method = checkerInfo.getActionType().getMethod("byId", String.class);
                        Object o = method.invoke(null, splittedAction);
                        typedActions.add(o);
                    } catch (NoSuchMethodException e) {
                        throw new UnsupportedDynamicMethodSignature(method, "Not found static method byId(String) in " + checkerInfo.getActionType().getSimpleName());
                    } catch (IllegalArgumentException e) {
                        throw new UnsupportedDynamicMethodSignature(method, "No enum value for " + splittedAction + " in " + checkerInfo.getActionType().getSimpleName());
                    } catch (Exception e) {
                        throw new UnsupportedDynamicMethodSignature(method, "Cannot execute method buId(String) in " + checkerInfo.getActionType().getSimpleName());
                    }

                }
                return Optional.of(new TwoArgumentsRequestGenerator(checkerInfo, typedActions));
            }
        }
        return Optional.empty();
    }

    private void checkParameters() {
        // if actionPatternType = EMPTY than no List or Maps
        if (actionPatternType == EMPTY) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                if (checkerInfo.getResourceType().isAssignableFrom(parameterType) || checkerInfo.getActionType().isAssignableFrom(parameterType)) {
                    return;
                } else {
                    throw new UnsupportedDynamicMethodSignature(method, "Method signature expects no List or Map parameters. Try to use All or Any postfix");
                }
            }
        } else {
            // check generic types or List or Map
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
            throw new UnsupportedDynamicMethodSignature(method, "NotExpectedResultException required for ensure* methods");
        }
    }

    private void checkReturnType() {
        Class<?> returnType = method.getReturnType();
        if (methodType == MethodType.ENSURE) {
            if (!void.class.equals(returnType)) {
                throw new UnsupportedDynamicMethodSignature(method, "void required for ensure* methods");
            }
            // ok
        } else if (methodType == MethodType.IS) {
            if (!boolean.class.equals(returnType) || Boolean.class.equals(returnType)) {
                throw new UnsupportedDynamicMethodSignature(method, "boolean required for is* methods");
            }
        }
    }


}
