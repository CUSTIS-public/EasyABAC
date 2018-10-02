package custis.easyabac.core.model.abac.attribute;

import java.util.List;

/**
 * Группа атрибутов в запросе
 */
public class AttributeGroup {
    private final String id;
    private final Category category;
    private final List<AttributeValue> attributes;

    public AttributeGroup(String id, Category category, List<AttributeValue> attributes) {
        this.id = id;
        this.category = category;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public List<AttributeValue> getAttributes() {
        return attributes;
    }

    public Category getCategory() {
        return category;
    }
}
