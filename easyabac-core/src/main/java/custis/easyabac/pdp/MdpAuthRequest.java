package custis.easyabac.pdp;

import custis.easyabac.core.model.abac.attribute.AttributeGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

public class MdpAuthRequest {
    private List<AttributeGroup> attributeGroups = new ArrayList<>();
    private List<MdpAuthRequest.RequestReference> requests = new ArrayList<>();

    public List<AttributeGroup> getAttributeGroups() {
        return unmodifiableList(attributeGroups);
    }

    public List<RequestReference> getRequests() {
        return unmodifiableList(requests);
    }

    public void addGroup(AttributeGroup group) {
        attributeGroups.add(group);
    }

    public void addReferenceToAllRequests(String groupId) {
        requests.forEach(requestReference -> requestReference.addRef(groupId));
    }

    public void addRequest(RequestReference reference) {
        requests.add(reference);
    }

    public static class RequestReference {
        private Set<String> requestIds = new HashSet<>();

        public Set<String> getRequestIds() {
            return unmodifiableSet(requestIds);
        }

        public void addRef(String ref) {
            requestIds.add(ref);
        }
    }
}
