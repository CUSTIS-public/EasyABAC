package custis.easyabac.api.core.call.converters;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.call.DecisionType;
import custis.easyabac.api.core.call.GettingReturnType;
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
    public Object convert(List<Object> arguments, Map<RequestId, AuthResponse> responses) {
        if (returningList) {
            // single entity, return list
            List<Object> returnList = new ArrayList<>();

            for (Map.Entry<RequestId, AuthResponse> entry : responses.entrySet()) {
                AuthResponse authResponse = entry.getValue();
                if (authResponse.getDecision() !=  decisionType.getDecision()) {
                    continue;
                }
             //   returnList.add(findValuesByResult(entry.getKey()));
            }

            return returnList;
        } else {
            Map<Object, List<Object>> returnMap = new HashMap<>();

            for (Map.Entry<RequestId, AuthResponse> entry : responses.entrySet()) {
                AuthResponse authResponse = entry.getValue();
                if (authResponse.getDecision() !=  decisionType.getDecision()) {
                    continue;
                }
        //        returnMap.put(entry.getKey(), findValuesByResult(entry.getKey()));
            }

            return returnMap;
        }

    }

}
