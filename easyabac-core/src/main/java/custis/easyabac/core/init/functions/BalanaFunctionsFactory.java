package custis.easyabac.core.init.functions;

import custis.easyabac.core.init.BalanaPolicyBuildException;
import custis.easyabac.core.model.abac.attribute.DataType;

public class BalanaFunctionsFactory {
    public static BalanaFunctions getFunctions(DataType dataType) {
        switch (dataType) {
            case STRING:
                return new BalanaStringFunctions();
            default:
                throw new BalanaPolicyBuildException("Functions for data type " + dataType + " are not supported yet");
        }
    }
}