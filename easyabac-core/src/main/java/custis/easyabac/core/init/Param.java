package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.attribute.Attribute;

import java.util.Objects;

public class Param {
    private final String name;
    private final Attribute attributeParam;
    private String value;

    public Param(String name, Attribute attributeParam) {
        this.name = name;
        this.attributeParam = attributeParam;
    }

    public String getName() {
        return name;
    }

    public Attribute getAttributeParam() {
        return attributeParam;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Param param = (Param) o;
        return Objects.equals(name, param.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
