package custis.easyabac.core.pdp;

import custis.easyabac.model.attribute.Attribute;
import custis.easyabac.model.attribute.AttributeWithValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptimizeMultiAuthRequest extends MultiAuthRequest {

    private final Set<AttributeWithValue> attributesWithValue;

    public OptimizeMultiAuthRequest(Map<String, Attribute> attributes, Set<AttributeWithValue> attributesWithValue, Map<RequestId, List<AttributeWithValue>> requests) {
        super(attributes, requests);
        this.attributesWithValue = attributesWithValue;
    }

}
