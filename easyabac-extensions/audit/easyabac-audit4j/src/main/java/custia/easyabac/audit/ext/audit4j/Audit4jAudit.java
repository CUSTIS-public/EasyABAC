package custia.easyabac.audit.ext.audit4j;

import custis.easyabac.core.audit.Audit;
import custis.easyabac.core.audit.DefaultAudit;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.model.attribute.AttributeWithValue;
import org.audit4j.core.AuditManager;
import org.audit4j.core.IAuditManager;
import org.audit4j.core.dto.AuditEvent;
import org.audit4j.core.dto.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Audit4jAudit implements Audit {

    public static final Logger log = LoggerFactory.getLogger(DefaultAudit.class);

    public static Audit4jAudit INSTANCE = new Audit4jAudit();

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

    private static AuditEvent createAuditEvent(List<AttributeWithValue> subject, AttributeWithValue action, AuthResponse result) {
        return new AuditEvent(serializeSubject(subject), action.getValues().get(0), decisionField(result), resourceField());
    }

    private static Field resourceField() {
        return new Field("resource", "resource"); // TODO implement
    }

    private static Field decisionField(AuthResponse result) {
        return new Field("decision", result.getDecision().name());
    }

    private static String serializeSubject(List<AttributeWithValue> subject) {
        return subject.toString();
    }
}