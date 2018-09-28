package custis.easyabac.pdp;

import java.util.List;
import java.util.stream.Collectors;

public class AttributiveAuthorizationServiceImpl implements AttributiveAuthorizationService {

    @Override
    public AuthResponse authorize(List<AuthAttribute> attributes) {
        System.out.println("Simple request");
        for (AuthAttribute attribute : attributes) {
            System.out.println(attribute.getId() + " = " + attribute.getValue());
        }
        System.out.println("--------------------------------");
        return new AuthResponse();
    }

    @Override
    public List<AuthResponse> authorizeMultiple(List<List<AuthAttribute>> attributes) {
        System.out.println("MDP request");
        return attributes.stream()
                .map(authAttributes -> authorize(authAttributes))
                .collect(Collectors.toList());
    }
}
