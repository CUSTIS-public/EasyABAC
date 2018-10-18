package custis.easyabac.core.pdp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SamplePDPHandler {

    private final AuthResponse.Decision decision;

    public SamplePDPHandler(AuthResponse.Decision decision) {
        this.decision = decision;
    }

    public Map<RequestId, AuthResponse> execute(Map<RequestId, List<AuthAttribute>> attributes) {
        System.out.println("MDP request");
        return attributes.entrySet()
                .stream()
                .collect(Collectors.toMap(o -> o.getKey(), o -> authorize(o.getValue())));
    }

    private AuthResponse authorize(List<AuthAttribute> attributes) {
        System.out.println("Simple request");
        for (AuthAttribute attribute : attributes) {
            System.out.println(attribute.getId() + " = " + attribute.getValues());
        }
        System.out.println("--------------------------------");
        return new AuthResponse(decision.DENY, Collections.emptyMap());
    }
}
