package custis.easyabac.generation.util.algorithm;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.Function;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class FunctionUtils {

    public static final String UNKNOWN_PREFIX = "!N/A!-";
    public static final String NEGATE_PREFIX = "!NOT!-";
    public static final String ACTION = "!ACTION!";

    public static String newUnknownResult() {
        return UNKNOWN_PREFIX + RandomStringUtils.randomAlphanumeric(5);
    }

    public static String generateValue(Function function, List<String> values, boolean expectedResult) throws EasyAbacInitException {
        switch (function) {
            case EQUAL:
                return generateEqualValue(values, expectedResult);
        }
        throw new IllegalStateException("Function " + function.getEasyName() + " is not implemented");
    }

    private static String generateEqualValue(List<String> values, boolean expectedResult) throws EasyAbacInitException {
        if (values.size() > 1) {
            throw new EasyAbacInitException("Function " + Function.EQUAL.getEasyName() + " should have 1 argument");
        }
        String value = values.get(0);
        if (expectedResult) {
            return value;
        } else {
            return newUnknownResult();
        }
    }
}
