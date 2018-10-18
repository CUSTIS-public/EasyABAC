package custis.easyabac.core.extend;


import custis.easyabac.model.attribute.AttributeWithValue;

import java.util.List;

public interface RequestExtender {

    void extend(List<AttributeWithValue> attributeWithValues);

}
