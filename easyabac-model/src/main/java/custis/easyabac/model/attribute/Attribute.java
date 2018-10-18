package custis.easyabac.model.attribute;

import java.util.Collections;
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

    public Attribute(String id, DataType type, Category category, boolean multiple) {
        this(id, type, category, multiple, id, Collections.emptySet(), id);
    }

    public Attribute(String id, DataType type, Category category, boolean multiple, String title, Set<String> allowableValues, String xacmlName) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.multiple = multiple;
        this.title = title;
        this.allowableValues = allowableValues;
        this.xacmlName = xacmlName;
    }

    public static final Attribute ACTION_ID = new Attribute("action-id", Category.ACTION, DataType.STRING);
    public static final Attribute RESOURCE_ID = new Attribute("resource-id", Category.RESOURCE, DataType.STRING);
    public static final Attribute SUBJECT_ID = new Attribute("subject-id", Category.SUBJECT, DataType.STRING);

    public Attribute(String id, Category category, DataType type) {
        this.id = id;
        this.title = id;
        this.category = category;
        this.type = type;
        this.multiple = false;
        this.allowableValues = Collections.emptySet();
        this.xacmlName = id;
    }

    public Attribute(String id) {
        this(id, DataType.STRING, Category.RESOURCE, false);
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
