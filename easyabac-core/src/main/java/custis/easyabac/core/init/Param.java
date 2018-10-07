package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.attribute.Attribute;

import java.util.Objects;

public class Param {
    private final String name;
    private final String attributeParamId;
    private String value;
    private Attribute attributeParam;

    public Param(String name, String attributeParamId) {
        this.name = name;
        this.attributeParamId = attributeParamId;
    }

    public String getName() {
        return name;
    }

    public String getAttributeParamId() {
        return attributeParamId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Attribute getAttributeParam() {
        return attributeParam;
    }

    public void setAttributeParam(Attribute attributeParam) {
        this.attributeParam = attributeParam;
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
