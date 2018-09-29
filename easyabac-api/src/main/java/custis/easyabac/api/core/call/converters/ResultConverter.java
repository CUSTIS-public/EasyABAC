package custis.easyabac.api.core.call.converters;

import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.util.List;
import java.util.Map;

public  interface ResultConverter {

    Object convert(List<Object> arguments, Map<RequestId, AuthResponse> responses);

}
