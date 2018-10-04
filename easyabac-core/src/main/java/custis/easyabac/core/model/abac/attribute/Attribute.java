package custis.easyabac.core.model.abac.attribute;

import java.util.Objects;
import java.util.Set;

public class Attribute {
    private final String id;
    private final DataType type;
    private final Category category;
    private final boolean multiple;

    private final String title;
    private final Set<String> allowableValues;
    private final String xacmlName;


    public Attribute(String id, DataType type, Category category, boolean multiple, String title, Set<String> allowableValues, String xacmlName) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.multiple = multiple;
        this.title = title;
        this.allowableValues = allowableValues;
        this.xacmlName = xacmlName;
    }

    public String getId() {
        return id;
    }


    public Category getCategory() {
        return category;
    }


    public DataType getType() {
        return type;
    }


    public boolean isMultiple() {
        return multiple;
    }


    public Set<String> getAllowableValues() {
        return allowableValues;
    }


    public String getTitle() {
        return title;
    }


    public String getXacmlName() {
        return xacmlName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return Objects.equals(id, attribute.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
