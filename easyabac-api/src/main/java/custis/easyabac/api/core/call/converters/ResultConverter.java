package custis.easyabac.api.core.call.converters;

import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.util.Map;

public  interface ResultConverter {

    Object convert(Object[] arguments, Map<RequestId, AuthResponse> responses);

}
