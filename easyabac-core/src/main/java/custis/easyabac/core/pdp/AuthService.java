package custis.easyabac.core.pdp;

import java.util.List;
import java.util.Map;

public interface AuthService {

    AuthResponse authorize(List<AuthAttribute> attributes);

    Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes);
}
