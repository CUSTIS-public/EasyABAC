package custis.easyabac.api.core.call;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.util.Map;

public class CheckingResultConverter implements ResultConverter {

    private final MethodType methodType;
    private final DecisionType decisionType;
    private final ActionPatternType actionPatternType;

    public CheckingResultConverter(MethodType methodType, DecisionType decisionType, ActionPatternType actionPatternType) {
        this.methodType = methodType;
        this.decisionType = decisionType;
        this.actionPatternType = actionPatternType;
    }

    public Object convert(Map<RequestId, AuthResponse> responses) {
        boolean check = false;
        for (AuthResponse response : responses.values()) {
            if (response.getResult().equals(decisionType.getAuthResult())) {
                if (actionPatternType == ActionPatternType.ANY) {
                    check = true;
                    break;
                } else {
                    check = true;
                }
            } else {
                if (actionPatternType == ActionPatternType.ALL) {
                    check = false;
                    break;
                }
            }
        }

        if (check && methodType == MethodType.IS) {
            return true;
        }

        if (!check) {
            if (methodType == MethodType.ENSURE) {
                throw new NotExpectedResultException(decisionType.getAuthResult(), "Not expected");
            } else {
                return false;
            }
        }
        return Void.class;
    }
}
