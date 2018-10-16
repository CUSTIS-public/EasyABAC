package custis.easyabac.core.audit;

import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthRequest;
import custis.easyabac.pdp.MdpAuthResponse;
import org.audit4j.core.AuditManager;
import org.audit4j.core.IAuditManager;
import org.audit4j.core.dto.AuditEvent;
import org.audit4j.core.dto.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Audit4jAudit implements Audit {

    public static final Logger LOGGER = LoggerFactory.getLogger(Audit4jAudit.class);

    public static Audit4jAudit INSTANCE = new Audit4jAudit();

    public static final IAuditManager auditManager = AuditManager.getInstance();

    @Override
    public void onRequest(List<AttributeWithValue> attributeWithValues, AuthResponse response) {
        List<AttributeWithValue> subject = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.SUBJECT)
                .collect(Collectors.toList());

        Optional<AttributeWithValue> action = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.ACTION)
                .findFirst();

        auditManager.audit(createAuditEvent(subject, action.get(), response));
    }

    @Override
    public void onMultipleRequest(MdpAuthRequest requestContext, MdpAuthResponse response) {
        List<AttributeWithValue> subject = requestContext.getAttributeGroups()
                .stream()
                .filter(attributeGroup -> attributeGroup.getCategory() == Category.SUBJECT)
                .flatMap(attributeGroup -> attributeGroup.getAttributes().stream())
                .collect(Collectors.toList());

        List<AttributeWithValue> actions = requestContext.getAttributeGroups()
                .stream()
                .filter(attributeGroup -> attributeGroup.getCategory() == Category.ACTION)
                .flatMap(attributeGroup -> attributeGroup.getAttributes().stream())
                .collect(Collectors.toList());

        response.getResults().entrySet().forEach(entry -> {
            for (AttributeWithValue action : actions) {
                auditManager.audit(createAuditEvent(subject, action, entry.getValue()));
            }

        });
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
