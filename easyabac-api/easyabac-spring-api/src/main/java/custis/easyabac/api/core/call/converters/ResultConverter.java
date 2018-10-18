package custis.easyabac.api.core.call.converters;

import custis.easyabac.api.utils.ResourceActionPair;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.RequestId;

import java.util.Map;

public  interface ResultConverter {

    Object convert(Map<RequestId, ResourceActionPair> mapping, Map<RequestId, AuthResponse> responses);

}
