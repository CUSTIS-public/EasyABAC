package custis.easyabac.core.model.abac.attribute;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        validateAttributes(attributes);
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

    public void addAttributes(List<AttributeValue> attributes) {
        validateAttributes(attributes);
        this.attributes.addAll(attributes);
    }

    private void validateAttributes(List<AttributeValue> attributes) {
        Optional<AttributeValue> incorrectCategory = attributes.stream()
                .filter(attributeValue -> !attributeValue.getAttribute().getCategory().equals(category))
                .findFirst();
        if (incorrectCategory.isPresent()) {
            throw new IllegalArgumentException("Attribute [" + incorrectCategory.get().getAttribute().getId() + "] should be " + category);
        }
    }

    public void addAttribute(AttributeValue attributeValue) {
        this.addAttributes(Arrays.asList(attributeValue));
    }
}
