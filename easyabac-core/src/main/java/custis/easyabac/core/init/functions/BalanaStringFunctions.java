package custis.easyabac.core.init.functions;

import org.wso2.balana.cond.*;

public class BalanaStringFunctions implements BalanaFunctions {

    @Override
    public Function equal() {
        return new GeneralBagFunction("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only");
    }

    @Override
    public Function greater() {
        return new ComparisonFunction(ComparisonFunction.NAME_STRING_GREATER_THAN);
    }

    @Override
    public Function less() {
        return new ComparisonFunction(ComparisonFunction.NAME_STRING_LESS_THAN);
    }

    @Override
    public Function greaterOrEqual() {
        return new ComparisonFunction(ComparisonFunction.NAME_STRING_GREATER_THAN_OR_EQUAL);

    }

    @Override
    public Function lessOrEqual() {
        return new ComparisonFunction(ComparisonFunction.NAME_STRING_LESS_THAN_OR_EQUAL);
    }

    @Override
    public Function in() {
        return null;
    }

    @Override
    public Function oneOf() {
        return null;
    }

    @Override
    public Function subset() {
        return null;
    }
}
