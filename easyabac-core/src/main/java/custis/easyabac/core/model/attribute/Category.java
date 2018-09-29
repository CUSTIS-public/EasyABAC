package custis.easyabac.core.model.attribute;


public enum Category {
    RESOURCE("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"),
    SUBJECT("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"),
    ACTION("urn:oasis:names:tc:xacml:3.0:attribute-category:action"),
    ENT_CATEGORY("urn:oasis:names:tc:xacml:3.0:attribute-category:environment");

    private String xacmlName;

    Category(String xacmlName) {
        this.xacmlName = xacmlName;
    }

    public String getXacmlName() {
        return xacmlName;
    }
}
