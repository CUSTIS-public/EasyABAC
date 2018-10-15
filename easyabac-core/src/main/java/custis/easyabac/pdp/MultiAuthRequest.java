package custis.easyabac.pdp;

import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;

import java.util.List;
import java.util.Map;

public class MultiAuthRequest {
    private final Map<String, Attribute> attributes;
    private final Map<RequestId, List<AttributeWithValue>> requests;

    public MultiAuthRequest(Map<String, Attribute> attributes, Map<RequestId, List<AttributeWithValue>> requests) {
        this.attributes = attributes;
        this.requests = requests;
    }

    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public Map<RequestId, List<AttributeWithValue>> getRequests() {
        return requests;
    }
}
