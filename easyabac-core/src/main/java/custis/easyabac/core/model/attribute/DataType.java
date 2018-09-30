package custis.easyabac.core.model.attribute;

public enum DataType {
    STRING("http://www.w3.org/2001/XMLSchema#string"),
    INT("http://www.w3.org/2001/XMLSchema#integer"),
    BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean"),
    DATE_TIME("http://www.w3.org/2001/XMLSchema#dateTime"),
    TIME("http://www.w3.org/2001/XMLSchema#time"),
    DATE("http://www.w3.org/2001/XMLSchema#date");

    private final String xacmlName;

    DataType(String xacmlName) {
        this.xacmlName = xacmlName;
    }

    public String getXacmlName() {
        return xacmlName;
    }

}
