package custis.easyabac.core.model.attribute;

import java.util.Set;

public class Attribute {
    private String id;
    private String title;
    private Category category;
    private DataType type;
    private boolean multiple = false;
    private Set<String> allowableValues;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
