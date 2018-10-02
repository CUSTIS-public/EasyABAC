package custis.easyabac.pdp;

import java.util.Collections;
import java.util.Map;

public class MdpAuthResponse {

    private Map<RequestId, AuthResponse> results = Collections.emptyMap();

    public Map<RequestId, AuthResponse> getResults() {
        return results;
    }
}
