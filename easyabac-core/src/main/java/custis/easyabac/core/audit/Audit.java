package custis.easyabac.core.audit;

import custis.easyabac.pdp.AuthResponse;

import java.util.Map;

public interface Audit {
    void onRequest(String actor, String action);

    void onMultipleRequest(String actor, Map<String, AuthResponse> actionResponse);
}
