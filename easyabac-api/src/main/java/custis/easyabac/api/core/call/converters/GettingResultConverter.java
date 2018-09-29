package custis.easyabac.api.core.call.converters;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.call.DecisionType;
import custis.easyabac.api.core.call.MethodType;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GettingResultConverter implements ResultConverter {

    private final MethodType methodType;
    private final DecisionType decisionType;
    private final PermissionCheckerInformation permissionCheckerInformation;

    public GettingResultConverter(MethodType methodType, DecisionType decisionType, PermissionCheckerInformation permissionCheckerInformation) {
        this.methodType = methodType;
        this.decisionType = decisionType;
        this.permissionCheckerInformation = permissionCheckerInformation;
    }

    public Object convert(Object[] arguments, Map<RequestId, AuthResponse> responses) {
        Object firstArgument = arguments[0];
        if (firstArgument.getClass().isAssignableFrom(permissionCheckerInformation.getResourceType())) {
            // single entity, return list

            List<?> returnList = responses.values()
                    .stream()
                    .filter(authResponse -> authResponse.getDecision() == decisionType.getDecision())
                    .collect(Collectors.toList());

            return returnList;
        } else {
            Map<?, ?> returnMap = new HashMap<>();
            // TODO
            return returnMap;
        }

    }
}
