package custis.easyabac.generation.algorithm;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.Function;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class FunctionUtils {

    public static final String UNKNOWN_PREFIX = "!N/A!-";
    public static final String ACTION = "!ACTION!";
    public static final String ANY_FUNCTION = "!f!-";
    public static final int FUNCTION_CODE_LENGTH = 4;
    public static final String NOT_EQUAL = ANY_FUNCTION + "neq-";
    public static final String EQUAL = ANY_FUNCTION + "eq--";
    public static final String GREATER = ANY_FUNCTION + "gr--";
    public static final String LESS = ANY_FUNCTION + "ls--";
    public static final String GREATER_OR_EQUAL = ANY_FUNCTION + "gre-";
    public static final String LESS_OR_EQUAL = ANY_FUNCTION + "lse-";

    public static String newUnknownResult() {
        return UNKNOWN_PREFIX + RandomStringUtils.randomAlphanumeric(5);
    }

    public static String generateValue(Function function, List<String> values, boolean expectedResult) throws EasyAbacInitException {
        switch (function) {
            case EQUAL:
                return generateEqualValue(values, expectedResult);
            case GREATER:
                return generateLessOrEqualValue(values, expectedResult);
            case LESS:
                return generateGreaterOrEqualsValue(values, expectedResult);
            case GREATER_OR_EQUAL:
                return generateLessValue(values, expectedResult);
            case LESS_OR_EQUAL:
                return generateGreaterValue(values, expectedResult);
        }
        throw new IllegalStateException("Function " + function.getEasyName() + " is not implemented");
    }

    private static String generateEqualValue(List<String> values, boolean expectedResult) throws EasyAbacInitException {
        if (values.size() > 1) {
            throw new EasyAbacInitException("Function " + Function.EQUAL.getEasyName() + " should have 1 argument");
        }
        String value = values.get(0);
        if (expectedResult) {
            if (value.startsWith(EQUAL)) {
                return value;
            } else {
                return EQUAL + value;
            }
        } else {
            return NOT_EQUAL + value;
        }
    }

    private static String generateGreaterValue(List<String> values, boolean expectedResult) throws EasyAbacInitException {
        if (values.size() > 1) {
            throw new EasyAbacInitException("Function " + Function.GREATER.getEasyName() + " should have 1 argument");
        }
        String value = values.get(0);
        if (expectedResult) {
            return GREATER + value;
        } else {
            return EQUAL + value; // will be equals
        }
    }

    private static String generateLessValue(List<String> values, boolean expectedResult) throws EasyAbacInitException {
        if (values.size() > 1) {
            throw new EasyAbacInitException("Function " + Function.LESS_OR_EQUAL.getEasyName() + " should have 1 argument");
        }
        String value = values.get(0);
        if (expectedResult) {
            return LESS + value;
        } else {
            return EQUAL + value; // will be equals
        }
    }

    private static String generateGreaterOrEqualsValue(List<String> values, boolean expectedResult) throws EasyAbacInitException {
        if (values.size() > 1) {
            throw new EasyAbacInitException("Function " + Function.GREATER_OR_EQUAL.getEasyName() + " should have 1 argument");
        }
        String value = values.get(0);
        if (expectedResult) {
            return GREATER + value;
        } else {
            return EQUAL + value;
        }
    }

    private static String generateLessOrEqualValue(List<String> values, boolean expectedResult) throws EasyAbacInitException {
        if (values.size() > 1) {
            throw new EasyAbacInitException("Function " + Function.LESS.getEasyName() + " should have 1 argument");
        }
        String value = values.get(0);
        if (expectedResult) {
            return LESS + value;
        } else {
            return EQUAL + value;
        }
    }

    private static Object greaterByType(Object nestedValue) {
        if (nestedValue instanceof String) {
            return nestedValue + "!";
        } else if (nestedValue instanceof Integer) {
            return (Integer) nestedValue + 1;
        }
        throw new UnsupportedOperationException(nestedValue.getClass() + " not supported");
    }

    private static Object lessByType(Object nestedValue) {
        if (nestedValue instanceof String) {
            return "!" + nestedValue;
        } else if (nestedValue instanceof Integer) {
            return (Integer) nestedValue - 1;
        }
        throw new UnsupportedOperationException(nestedValue.getClass() + " not supported");
    }

    public static Object calculateValue(Object nestedValue, String function) {
        if (function.equals(EQUAL)) {
            return nestedValue;
        } else if (function.equals(NOT_EQUAL)) {
            return nestedValue + "$";
        } else if (function.equals(GREATER)) {
            return FunctionUtils.greaterByType(nestedValue);
        }  else if (function.equals(LESS)) {
            return FunctionUtils.lessByType(nestedValue);
        } else if (function.equals(GREATER_OR_EQUAL)) {
            return nestedValue;
        } else if (function.equals(LESS_OR_EQUAL)) {
            return nestedValue;
        }
        throw new UnsupportedOperationException(function + " not supported");
    }
}
