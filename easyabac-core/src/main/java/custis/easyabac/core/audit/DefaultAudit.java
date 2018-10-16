package custis.easyabac.core.audit;

import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthRequest;
import custis.easyabac.pdp.MdpAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultAudit implements Audit {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultAudit.class);

    public static DefaultAudit INSTANCE = new DefaultAudit();

    @Override
    public void onRequest(List<AttributeWithValue> attributeWithValues, AuthResponse response) {
        if (LOGGER.isDebugEnabled()) {
            //LOGGER.debug("Audit " + attributeWithValues + ", response " + response);
        }
    }

    @Override
    public void onMultipleRequest(MdpAuthRequest requestContext, MdpAuthResponse response) {
        if (LOGGER.isDebugEnabled()) {
            for (AuthResponse value : response.getResults().values()) {
                //LOGGER.debug("Audit " + requestContext + ", response " + response);
            }
        }
    }

}
