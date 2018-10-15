package custis.easyabac.core.init.functions;

import custis.easyabac.core.init.BalanaPolicyBuildException;
import org.wso2.balana.cond.ConditionBagFunction;
import org.wso2.balana.cond.EqualFunction;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.GeneralBagFunction;

public class BalanaBooleanFunctions implements BalanaFunctions {

    @Override
    public Function equal() {
        return new EqualFunction(EqualFunction.NAME_BOOLEAN_EQUAL);
    }

    @Override
    public Function greater() {
        throw new BalanaPolicyBuildException("Unsupported function greater for boolean");
    }

    @Override
    public Function less() {
        throw new BalanaPolicyBuildException("Unsupported function less for boolean");
    }

    @Override
    public Function greaterOrEqual() {
        throw new BalanaPolicyBuildException("Unsupported function greaterOrEqual for boolean");
    }

    @Override
    public Function lessOrEqual() {
        throw new BalanaPolicyBuildException("Unsupported function lessOrEqual for boolean");
    }

    @Override
    public Function in() {
        return new ConditionBagFunction("urn:oasis:names:tc:xacml:1.0:function:boolean-is-in");
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
        return new GeneralBagFunction("urn:oasis:names:tc:xacml:1.0:function:boolean-bag");
    }

    @Override
    public Function oneAndOnly() {
        return new GeneralBagFunction("urn:oasis:names:tc:xacml:1.0:function:boolean-one-and-only");
    }
}
