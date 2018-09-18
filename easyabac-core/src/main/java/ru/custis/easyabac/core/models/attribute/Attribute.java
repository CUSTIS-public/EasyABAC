package ru.custis.easyabac.core.models.attribute;

import java.util.Set;

public class Attribute {
    private String code;
    private Category category;
    private DataType dataType;
    private boolean array;
    private Set<String> allowableValues;
}
