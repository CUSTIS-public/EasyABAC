package custis.easyabac.core.extend;

import custis.easyabac.core.model.abac.attribute.AttributeWithValue;

import java.util.List;

public interface RequestExtender {

    void extend(List<AttributeWithValue> attributeWithValues);

}
