package custis.easyabac.pdp;

import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;

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
