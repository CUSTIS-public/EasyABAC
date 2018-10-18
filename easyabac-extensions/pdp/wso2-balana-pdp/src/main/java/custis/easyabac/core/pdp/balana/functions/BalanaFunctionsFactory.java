package custis.easyabac.core.pdp.balana.functions;

import custis.easyabac.core.pdp.balana.BalanaPolicyBuildException;
import custis.easyabac.model.attribute.DataType;

public class BalanaFunctionsFactory {
    public static BalanaFunctions getFunctions(DataType dataType) {
        switch (dataType) {
            case STRING:
                return new BalanaStringFunctions();
            case INT:
                return new BalanaIntegerFunctions();
            case BOOLEAN:
                return new BalanaBooleanFunctions();
            case DATE_TIME:
                return new BalanaDateTimeFunctions();
            case DATE:
                return new BalanaDateFunctions();
            case TIME:
                return new BalanaTimeFunctions();
            default:
                throw new BalanaPolicyBuildException("Functions for data type " + dataType + " are not supported yet");
        }
    }
}