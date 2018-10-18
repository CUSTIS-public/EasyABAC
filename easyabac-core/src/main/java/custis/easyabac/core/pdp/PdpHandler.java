package custis.easyabac.core.pdp;

import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.AttributeWithValue;

import java.util.List;

public interface PdpHandler {
    AuthResponse evaluate(List<AttributeWithValue> attributeWithValues);

    boolean xacmlPolicyMode();

    MultiAuthResponse evaluate(MultiAuthRequest multiAuthRequest) throws EasyAbacInitException;

    MultiAuthResponse evaluate(MultiAuthRequestOptimize multiAuthRequest) throws EasyAbacInitException;
}
