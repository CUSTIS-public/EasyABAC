package custis.easyabac.pdp;

import java.util.Map;

public class MdpAuthResponse {

    private Map<RequestId, AuthResponse> results;

    public MdpAuthResponse(Map<RequestId, AuthResponse> results) {
        this.results = results;
    }

    public Map<RequestId, AuthResponse> getResults() {
        return results;
    }
}
