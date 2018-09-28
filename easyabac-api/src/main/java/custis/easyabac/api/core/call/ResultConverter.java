package custis.easyabac.api.core.call;

import custis.easyabac.pdp.AuthResponse;

import java.util.List;

public  interface ResultConverter {

    Object convert(List<AuthResponse> responses);

}
