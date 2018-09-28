package custis.easyabac.pdp;

import java.util.List;
import java.util.Map;

public interface AttributiveAuthorizationService {

    AuthResponse authorize(List<AuthAttribute> attributes);

    Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes);
}
