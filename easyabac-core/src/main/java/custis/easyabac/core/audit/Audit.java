package custis.easyabac.core.audit;

import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MdpAuthRequest;
import custis.easyabac.pdp.MdpAuthResponse;

import java.util.List;

public interface Audit {
    void onRequest(List<AttributeWithValue> attributeWithValues, AuthResponse response);

    void onMultipleRequest(MdpAuthRequest requestContext, MdpAuthResponse response);
}
