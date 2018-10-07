package custis.easyabac.core.audit;

import custis.easyabac.pdp.AuthResponse;
import org.audit4j.core.AuditManager;
import org.audit4j.core.IAuditManager;
import org.audit4j.core.dto.AuditEvent;
import org.audit4j.core.dto.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultAudit implements Audit {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultAudit.class);

    public static DefaultAudit INSTANCE = new DefaultAudit();

    public static final IAuditManager auditManager = AuditManager.getInstance();

    @Override
    public void onRequest(String actor, String action) {
        auditManager.audit(createAuditEvent(actor, action, null));
    }

    @Override
    public void onMultipleRequest(String actor, Map<String, AuthResponse> actionResponse) {
        actionResponse.entrySet().forEach(entry -> auditManager.audit(createAuditEvent(actor, entry.getKey(), entry.getValue())));
    }

    private static AuditEvent createAuditEvent(String actor, String action, AuthResponse result) {
        return new AuditEvent(actor, action, decisionField(result), resourceField());
    }

    private static Field resourceField() {
        return new Field("resource", "resource"); // TODO implement
    }

    private static Field decisionField(AuthResponse result) {
        return new Field("decision", result.getDecision().name());
    }
}
