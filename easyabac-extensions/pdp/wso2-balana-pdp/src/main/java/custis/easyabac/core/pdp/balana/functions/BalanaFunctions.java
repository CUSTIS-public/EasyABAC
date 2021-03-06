package custis.easyabac.core.pdp.balana.functions;

import custis.easyabac.core.pdp.balana.BalanaPolicyBuildException;
import custis.easyabac.model.attribute.DataType;
import org.wso2.balana.cond.Function;

import static custis.easyabac.model.Function.*;

public interface BalanaFunctions {
    Function equal();
    Function greater();
    Function less();
    Function greaterOrEqual();
    Function lessOrEqual();
    Function in();
    Function oneOf();
    Function subset();
    Function bag();
    Function oneAndOnly();

    default boolean isSupported(custis.easyabac.model.Function f) {
        return true;
    }

    DataType supportedType();

    default Function pick(custis.easyabac.model.Function f) {
        if (!isSupported(f)) {
            throw new BalanaPolicyBuildException(String.format("Function %s is not supported for type %s", f, supportedType()));
        }

        switch (f) {
            case EQUAL:
                return equal();
            case GREATER_OR_EQUAL:
                return greaterOrEqual();
            case GREATER:
                return greater();
            case LESS_OR_EQUAL:
                return lessOrEqual();
            case LESS:
                return less();
            case IN:
                return in();
            case ONE_OF:
                return oneOf();
            case SUBSET:
                return subset();
            default:
                throw new BalanaPolicyBuildException(String.format("Unknown function: %s", f));
        }
    }

    default boolean requiresBagOfValues(custis.easyabac.model.Function f) {
        return f == IN || f == ONE_OF || f == SUBSET;
    }

    default boolean requiresRightBagAttribute(custis.easyabac.model.Function f) {
        return f == IN || f == ONE_OF || f == SUBSET;
    }

    default boolean requiresLeftBagAttribute(custis.easyabac.model.Function f) {
        return f == ONE_OF || f == SUBSET;
    }
}