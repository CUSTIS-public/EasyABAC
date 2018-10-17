package custis.easyabac.core.audit;

import custis.easyabac.pdp.AuthResponse;

import java.util.Map;

public class DefaultAudit implements Audit {

    public static DefaultAudit INSTANCE = new DefaultAudit();

    @Override
    public void onAction(String actor, Map<String, String> resource, String action, AuthResponse.Decision decision) {

    }

    @Override
    public void onMultipleActions(String actor, Map<String, String> resource, Map<String, AuthResponse.Decision> actionResponse) {

    }

}
