package custis.easyabac.core.model.abac.attribute;


import custis.easyabac.core.init.EasyAbacInitException;

public enum Category {
    RESOURCE("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"),
    SUBJECT("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"),
    ACTION("urn:oasis:names:tc:xacml:3.0:attribute-category:action"),
    ENV("urn:oasis:names:tc:xacml:3.0:attribute-category:environment");

    private String xacmlName;

    Category(String xacmlName) {
        this.xacmlName = xacmlName;
    }

    public String getXacmlName() {
        return xacmlName;
    }

    public static Category findByXacmlName(String xacmlName) throws EasyAbacInitException {
        for (Category value : Category.values()) {
            if (xacmlName.equals(value.getXacmlName())) {
                return value;
            }
        }
        throw new EasyAbacInitException("Category " + xacmlName + " is not supported");

    }
}
