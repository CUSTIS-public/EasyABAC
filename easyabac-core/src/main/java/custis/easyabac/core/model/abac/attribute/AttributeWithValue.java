package custis.easyabac.core.model.abac.attribute;

import java.util.List;

public class AttributeWithValue {
    private final Attribute attribute;
    private final List<String> values;

    public AttributeWithValue(Attribute attribute, List<String> values) {
        this.attribute = attribute;
        this.values = values;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public List<String> getValues() {
        return values;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeWithValue that = (AttributeWithValue) o;

        if (!attribute.equals(that.attribute)) return false;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = attribute.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }
}
