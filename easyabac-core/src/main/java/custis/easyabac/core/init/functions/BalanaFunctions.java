package custis.easyabac.core.init.functions;

import custis.easyabac.core.init.BalanaPolicyBuildException;
import org.wso2.balana.cond.Function;

public interface BalanaFunctions {
    Function equal();
    Function greater();
    Function less();
    Function greaterOrEqual();
    Function lessOrEqual();
    Function in();
    Function oneOf();
    Function subset();

    default boolean isSupported(custis.easyabac.core.model.abac.Function f) {
        return true;
    }

    default Function pick(custis.easyabac.core.model.abac.Function f) {
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
                throw new BalanaPolicyBuildException("Unsupported function: " + f);
        }
    }
}