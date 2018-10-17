package custis.easyabac.pdp;

import java.util.List;
import java.util.Map;

public class MultiAuthRequestOptimize {
    private final Map<String, AuthAttribute> attributesWithValue;
    private final Map<RequestId, List<String>> requests;

    public MultiAuthRequestOptimize(Map<String, AuthAttribute> attributesWithValue, Map<RequestId, List<String>> requests) {
        this.attributesWithValue = attributesWithValue;
        this.requests = requests;
    }

    public Map<String, AuthAttribute> getAttributesWithValue() {
        return attributesWithValue;
    }

    public Map<RequestId, List<String>> getRequests() {
        return requests;
    }
}
