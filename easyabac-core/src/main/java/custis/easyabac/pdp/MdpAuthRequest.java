package custis.easyabac.pdp;

import custis.easyabac.core.model.abac.attribute.AttributeGroup;

import java.util.ArrayList;
import java.util.List;

public class MdpAuthRequest {
    private List<AttributeGroup> attributeGroups = new ArrayList<>();
    private List<MdpAuthRequest.RequestReference> requests = new ArrayList<>();

    public List<AttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

    public List<RequestReference> getRequests() {
        return requests;
    }

    public class RequestReference {
        private List<String> requestIds;

        public List<String> getRequestIds() {
            return requestIds;
        }
    }
}
