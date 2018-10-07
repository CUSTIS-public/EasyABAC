package custis.easyabac.core.audit;

import custis.easyabac.pdp.AuthResponse;
import org.audit4j.core.AuditManager;
import org.audit4j.core.IAuditManager;
import org.audit4j.core.dto.AuditEvent;
import org.audit4j.core.dto.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultAudit implements Audit {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultAudit.class);

    public static DefaultAudit INSTANCE = new DefaultAudit();

    public static final IAuditManager auditManager = AuditManager.getInstance();

    @Override
    public void onAction(String actor, Map<String, String> resource, String action, AuthResponse.Decision decision) {
        auditManager.audit(createAuditEvent(actor, resource, action, decision));
    }

    @Override
    public void onMultipleActions(String actor, Map<String, String> resource, Map<String, AuthResponse.Decision> actionResponse) {
        actionResponse.entrySet().forEach(entry -> auditManager.audit(createAuditEvent(actor, resource, entry.getKey(), entry.getValue())));
    }

    private static AuditEvent createAuditEvent(String actor, Map<String, String> resource, String action, AuthResponse.Decision decision) {
        List<Field> additionalFields = resourceFields(resource);
        additionalFields.add(decisionField(decision));
        return new AuditEvent(actor, action, additionalFields.toArray(new Field[additionalFields.size()]));
    }

    private static List<Field> resourceFields(Map<String, String> resource) {
        List<Field> fields = new ArrayList<>();
        for (Map.Entry<String, String> entry : resource.entrySet()) {
            fields.add(new Field(entry.getKey(), entry.getValue()));
        }
        return fields;
    }

    private static Field decisionField(AuthResponse.Decision decision) {
        return new Field("decision", decision.name());
    }
}
