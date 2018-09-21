package ru.custis.easyabac.core.models.attribute;

import java.util.Set;

public class Attribute {
    private String code;
    private Category category;
    private DataType dataType;
    private boolean array;
    private Set<String> allowableValues;

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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isArray() {
        return array;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public Set<String> getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(Set<String> allowableValues) {
        this.allowableValues = allowableValues;
    }
}
