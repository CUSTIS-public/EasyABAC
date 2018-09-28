package custis.easyabac.pdp;

import java.util.List;

public interface AttributiveAuthorizationService {

    AuthResponse authorize(List<AuthAttribute> attributes);

    List<AuthResponse> authorizeMultiple(List<List<AuthAttribute>> attributes);
}
