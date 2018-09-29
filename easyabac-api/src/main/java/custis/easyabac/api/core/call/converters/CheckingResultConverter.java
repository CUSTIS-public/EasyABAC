package custis.easyabac.api.core.call.converters;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.core.call.ActionPatternType;
import custis.easyabac.api.core.call.DecisionType;
import custis.easyabac.api.core.call.MethodType;
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

    public Object convert(Object[] arguments, Map<RequestId, AuthResponse> responses) {
        boolean check = false;
        for (AuthResponse response : responses.values()) {
            if (response.getDecision().equals(decisionType.getDecision())) {
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
                throw new NotExpectedResultException(decisionType.getDecision(), "Not expected");
            } else {
                return false;
            }
        }
        return Void.class;
    }
}
