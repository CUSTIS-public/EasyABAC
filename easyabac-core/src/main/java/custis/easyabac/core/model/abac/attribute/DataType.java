package custis.easyabac.core.model.abac.attribute;

import custis.easyabac.core.init.EasyAbacInitException;

public enum DataType {
    STRING("string", "http://www.w3.org/2001/XMLSchema#string"),
    INT("int", "http://www.w3.org/2001/XMLSchema#integer"),
    BOOLEAN("boolean", "http://www.w3.org/2001/XMLSchema#boolean"),
    DATE_TIME("dateTime", "http://www.w3.org/2001/XMLSchema#dateTime"),
    TIME("time", "http://www.w3.org/2001/XMLSchema#time"),
    DATE("date", "http://www.w3.org/2001/XMLSchema#date");

    private String easyName;
    private String xacmlName;

    DataType(String easyName, String xacmlName) {
        this.easyName = easyName;
        this.xacmlName = xacmlName;
    }

    public String getXacmlName() {
        return xacmlName;
    }

    public String getEasyName() {
        return easyName;
    }

    public static DataType findByEasyName(String easyName) throws EasyAbacInitException {
        for (DataType value : DataType.values()) {
            if (easyName.equals(value.getEasyName())) {
                return value;
            }
        }
        // TODO придумать подходящий тип
        throw new EasyAbacInitException("Type " + easyName + " not supported");

    }
}
