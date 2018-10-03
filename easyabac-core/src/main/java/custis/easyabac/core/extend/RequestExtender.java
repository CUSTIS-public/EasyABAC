package custis.easyabac.core.extend;

import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.pdp.MdpAuthRequest;

import java.util.List;

public interface RequestExtender {

    void extend(List<AttributeValue> attributeValues);

    void extend(MdpAuthRequest mdpAuthRequest);
}
