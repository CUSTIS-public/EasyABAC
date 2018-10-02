package custis.easyabac.core.extend.subject;

import custis.easyabac.core.model.abac.attribute.AttributeValue;

import java.util.List;

public interface SubjectAttributesProvider {

    List<AttributeValue> provide();
}
