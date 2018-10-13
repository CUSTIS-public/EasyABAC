package custis.easyabac.core.init.functions;

import org.wso2.balana.cond.*;

public class BalanaTimeFunctions implements BalanaFunctions {
    @Override
    public Function equal() {
        return new EqualFunction(EqualFunction.NAME_TIME_EQUAL);
    }

    @Override
    public Function greater() {
        return new ComparisonFunction(ComparisonFunction.NAME_TIME_GREATER_THAN);
    }

    @Override
    public Function less() {
        return new ComparisonFunction(ComparisonFunction.NAME_TIME_LESS_THAN);
    }

    @Override
    public Function greaterOrEqual() {
        return new ComparisonFunction(ComparisonFunction.NAME_TIME_GREATER_THAN_OR_EQUAL);
    }

    @Override
    public Function lessOrEqual() {
        return new ComparisonFunction(ComparisonFunction.NAME_TIME_LESS_THAN_OR_EQUAL);
    }

    @Override
    public Function in() {
        return new ConditionBagFunction("urn:oasis:names:tc:xacml:1.0:function:time-is-in");
    }

    @Override
    public Function oneOf() {
        return null;
    }

    @Override
    public Function subset() {
        return null;
    }

    @Override
    public Function bag() {
        return new GeneralBagFunction("urn:oasis:names:tc:xacml:1.0:function:time-bag");
    }

    @Override
    public Function oneAndOnly() {
        return new GeneralBagFunction("urn:oasis:names:tc:xacml:1.0:function:time-one-and-only");
    }

    @Override
    public boolean isSupported(custis.easyabac.core.model.abac.Function f) {
        return f != custis.easyabac.core.model.abac.Function.ONE_OF ||
                f != custis.easyabac.core.model.abac.Function.SUBSET;
    }
}
