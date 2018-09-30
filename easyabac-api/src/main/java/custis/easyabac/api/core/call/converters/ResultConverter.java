package custis.easyabac.api.core.call.converters;

import custis.easyabac.api.core.call.getters.ResourceActionPair;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.util.Map;

public  interface ResultConverter {

    Object convert(Map<RequestId, ResourceActionPair> mapping, Map<RequestId, AuthResponse> responses);

}
