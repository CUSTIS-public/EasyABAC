package custis.easyabac.pdp;

import custis.easyabac.core.model.abac.attribute.AttributeValue;

import java.util.ArrayList;
import java.util.List;

public class MdpAuthRequest {
    private List<AttributeValue> attributeValues = new ArrayList<>();
    private List<MdpAuthRequest.RequestReference> requests = new ArrayList<>();

    public List<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public List<RequestReference> getRequests() {
        return requests;
    }

    private class RequestReference {

    }
}
