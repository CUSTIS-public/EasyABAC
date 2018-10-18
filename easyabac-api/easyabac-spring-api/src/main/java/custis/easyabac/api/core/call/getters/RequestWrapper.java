package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.utils.ResourceActionPair;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.RequestId;

import java.util.List;
import java.util.Map;

public class RequestWrapper {

    private Map<RequestId, List<AuthAttribute>> requests;
    private Map<RequestId, ResourceActionPair> mapping;

    public RequestWrapper(Map<RequestId, List<AuthAttribute>> requests, Map<RequestId, ResourceActionPair> mapping) {
        this.requests = requests;
        this.mapping = mapping;
    }

    public Map<RequestId, List<AuthAttribute>> getRequests() {
        return requests;
    }

    public Map<RequestId, ResourceActionPair> getMapping() {
        return mapping;
    }
}
