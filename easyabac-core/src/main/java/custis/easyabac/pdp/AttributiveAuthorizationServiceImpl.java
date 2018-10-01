package custis.easyabac.pdp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributiveAuthorizationServiceImpl implements AttributiveAuthorizationService {

    private SamplePDPHandler pdpHandler = new SamplePDPHandler();

    @Override
    public AuthResponse authorize(List<AuthAttribute> attributes) {
        Map<RequestId, AuthResponse> resultMap = authorizeMultiple(new HashMap<RequestId, List<AuthAttribute>>() {
            {
                put(RequestId.newRandom(), attributes);
            }
        });
        return (AuthResponse) resultMap.values().toArray()[0];
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {
        // оптимизировать запрос
        // 1. выделить общие части и заменить синонимами
        // 2. может быть выполнить свертку (???)


        Map<RequestId, AuthResponse> result = pdpHandler.execute(attributes);

        // post processing
        // 1. аудит

        return result;
    }
}
