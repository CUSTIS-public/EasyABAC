package custis.easyabac.core.audit;

import custis.easyabac.pdp.AuthResponse;

import java.util.Map;

public interface Audit {
    void onAction(String actor, Map<String, String> resource, String action, AuthResponse.Decision decision);

    void onMultipleActions(String actor, Map<String, String> resource, Map<String, AuthResponse.Decision> actionResponse);
}
