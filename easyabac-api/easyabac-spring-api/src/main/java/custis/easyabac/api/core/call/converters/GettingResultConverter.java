package custis.easyabac.api.core.call.converters;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.call.DecisionType;
import custis.easyabac.api.core.call.GettingReturnType;
import custis.easyabac.api.utils.ResourceActionPair;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GettingResultConverter implements ResultConverter {

    private final DecisionType decisionType;
    private final GettingReturnType gettingReturnType;
    private final boolean returningList;
    private final PermissionCheckerInformation checkerInformation;

    public GettingResultConverter(DecisionType decisionType, GettingReturnType gettingReturnType, boolean returningList, PermissionCheckerInformation checkerInformation) {
        this.decisionType = decisionType;
        this.gettingReturnType = gettingReturnType;
        this.returningList = returningList;
        this.checkerInformation = checkerInformation;
    }

    @Override
    public Object convert(Map<RequestId, ResourceActionPair> mapping, Map<RequestId, AuthResponse> responses) {
        if (returningList) {
            return convertList(mapping, responses);
        } else {
            return convertMap(mapping, responses);
        }

    }

    private Object convertMap(Map<RequestId, ResourceActionPair> mapping, Map<RequestId, AuthResponse> responses) {
        Map<Object, List<Object>> returnMap = new HashMap<>();

        for (Map.Entry<RequestId, AuthResponse> entry : responses.entrySet()) {
            AuthResponse authResponse = entry.getValue();
            if (authResponse.getDecision() !=  decisionType.getDecision()) {
                continue;
            }

            ResourceActionPair pair = mapping.get(entry.getKey());
            if (gettingReturnType == GettingReturnType.RESOURCES) {
                returnMap
                        .computeIfAbsent(pair.getAction(), o -> new ArrayList<>())
                        .add(pair.getResource());
            } else if (gettingReturnType == GettingReturnType.ACTIONS) {
                returnMap
                        .computeIfAbsent(pair.getResource(), o -> new ArrayList<>())
                        .add(pair.getAction());
            }
        }

        return returnMap;
    }

    private Object convertList(Map<RequestId, ResourceActionPair> mapping, Map<RequestId, AuthResponse> responses) {
        // single entity, return list
        List<Object> returnList = new ArrayList<>();

        for (Map.Entry<RequestId, AuthResponse> entry : responses.entrySet()) {
            AuthResponse authResponse = entry.getValue();
            if (authResponse.getDecision() !=  decisionType.getDecision()) {
                continue;
            }

            ResourceActionPair pair = mapping.get(entry.getKey());
            if (gettingReturnType == GettingReturnType.RESOURCES) {
                returnList.add(pair.getResource());
            } else if (gettingReturnType == GettingReturnType.ACTIONS) {
                returnList.add(pair.getAction());
            }
        }

        return returnList;
    }

}
