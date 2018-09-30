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
import java.util.Arrays;
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
                if (splittedActions.length != 1) {
                    return Optional.of(new TwoArgumentsRequestGenerator(checkerInfo, Arrays.asList(splittedActions)));
                } else {
                    splittedActions = actionsString.toLowerCase().split(LEXEM_AND);
                    return Optional.of(new TwoArgumentsRequestGenerator(checkerInfo, Arrays.asList(splittedActions)));
                }
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
