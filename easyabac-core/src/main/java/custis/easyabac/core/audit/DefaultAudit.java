package custis.easyabac.core.audit;

import custis.easyabac.pdp.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultAudit implements Audit {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultAudit.class);

    public static DefaultAudit INSTANCE = new DefaultAudit();

    @Override
    public void onAction(String actor, Map<String, String> resource, String action, AuthResponse.Decision decision) {
        LOGGER.info(actor + resource + action + decision);
    }

    @Override
    public void onMultipleActions(String actor, Map<String, String> resource, Map<String, AuthResponse.Decision> actionResponse) {
        actionResponse.entrySet().forEach(entry -> LOGGER.info(actor + resource + entry.getKey() + entry.getValue()));
    }

}
