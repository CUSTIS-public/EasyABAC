package custis.easyabac.core.model.abac.attribute;

import java.util.List;

public class AttributeValue {
    private final Attribute attribute;
    private final List<String> values;

    public AttributeValue(Attribute attribute, List<String> values) {
        this.attribute = attribute;
        this.values = values;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public List<String> getValues() {
        return values;
    }
}
