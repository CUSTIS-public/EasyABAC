package custis.easyabac.api.core.call;

import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.util.Map;

public  interface ResultConverter {

    Object convert(Map<RequestId, AuthResponse> responses);

}
