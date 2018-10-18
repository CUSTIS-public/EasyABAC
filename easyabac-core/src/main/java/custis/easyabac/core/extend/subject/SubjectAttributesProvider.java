package custis.easyabac.core.extend.subject;

import custis.easyabac.model.attribute.AttributeWithValue;

import java.util.List;

public interface SubjectAttributesProvider {

    List<AttributeWithValue> provide();
}
