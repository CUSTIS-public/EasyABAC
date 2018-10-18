package custis.easyabac.api.impl;

import custis.easyabac.core.extend.subject.SubjectAttributesProvider;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.attribute.Attribute;
import custis.easyabac.model.attribute.AttributeWithValue;

import java.util.*;

public class EntitySubjectAttributesProvider<T> implements SubjectAttributesProvider {

    private final AbacAuthModel model;
    private final EntityGetter<T> entityGetter;

    public EntitySubjectAttributesProvider(AbacAuthModel model, EntityGetter<T> entityGetter) {
        this.model = model;
        this.entityGetter = entityGetter;
    }

    @Override
    public List<AttributeWithValue> provide() {
        Optional<T> optional = entityGetter.get();
        if (optional.isPresent()) {
            List<AuthAttribute> attributes = AttributeValueExtractor.extractAttributesFromSubject(optional.get());
            Map<String, Attribute> modelAttributes = model.getAttributes();
            List<AttributeWithValue> out = new ArrayList<>();
            for (AuthAttribute attribute : attributes) {
                Attribute modelAttribute = modelAttributes.get(attribute.getId());
                if (modelAttribute == null) {
                    continue;
                }
                out.add(new AttributeWithValue(modelAttribute, attribute.getValues()));
            }
            return out;
        } else {
            return Collections.emptyList();
        }
    }
}
