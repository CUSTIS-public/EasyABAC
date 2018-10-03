package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthRequest;
import custis.easyabac.pdp.MdpAuthResponse;

import java.util.List;

public interface PdpHandler {
    AuthResponse evaluate(List<AttributeValue> attributeValues);

    MdpAuthResponse evaluate(MdpAuthRequest mdpAuthRequest);
}
