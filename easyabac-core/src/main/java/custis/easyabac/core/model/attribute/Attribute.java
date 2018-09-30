package custis.easyabac.core.model.attribute;

import java.util.Collections;
import java.util.Set;

public class Attribute {
    private String code;
    private String title;
    private Category category;
    private DataType type;
    private boolean multiple = false;
    private Set<String> allowableValues;

    public static final Attribute ACTION_ID = new Attribute("action-id", Category.ACTION, DataType.STRING);
    public static final Attribute RESOURCE_ID = new Attribute("resource-id", Category.RESOURCE, DataType.STRING);
    public static final Attribute SUBJECT_ID = new Attribute("subject-id", Category.SUBJECT, DataType.STRING);

    public Attribute() {
    }

    public Attribute(String code, Category category, DataType type) {
        this.code = code;
        this.title = code;
        this.category = category;
        this.type = type;
        this.multiple = false;
        this.allowableValues = Collections.emptySet();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public Set<String> getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(Set<String> allowableValues) {
        this.allowableValues = allowableValues;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
