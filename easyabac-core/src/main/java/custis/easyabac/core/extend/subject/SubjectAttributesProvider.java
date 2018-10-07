package custis.easyabac.core.extend.subject;

import custis.easyabac.core.model.abac.attribute.AttributeWithValue;

import java.util.List;

public interface SubjectAttributesProvider {

    List<AttributeWithValue> provide();
}
