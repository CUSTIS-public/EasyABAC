package custis.easyabac.pdp;

import java.util.Map;

public class MultiAuthResponse {
    private final Map<RequestId, AuthResponse> results;

    public MultiAuthResponse(Map<RequestId, AuthResponse> results) {
        this.results = results;
    }

    public Map<RequestId, AuthResponse> getResults() {
        return results;
    }
}
