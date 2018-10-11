package custis.easyabac.pdp;

import custis.easyabac.core.trace.model.TraceResult;

import java.util.Collections;
import java.util.Map;

public class AuthResponse {

    private final Decision decision;
    private final Map<String, String> returnValues;
    private final String errorMsg;
    private final TraceResult traceResult;

    public AuthResponse(Decision decision, Map<String, String> returnValues) {
        this(decision, returnValues, null);
    }

    public AuthResponse(Decision decision, Map<String, String> returnValues, TraceResult traceResult) {
        this.decision = decision;
        this.returnValues = returnValues;
        this.traceResult = traceResult;
        this.errorMsg = null;
    }

    public AuthResponse(String errorMsg) {
        this.errorMsg = errorMsg;
        decision = Decision.INDETERMINATE;
        returnValues = Collections.emptyMap();
        traceResult = TraceResult.EMPTY;
    }

    public Decision getDecision() {
        return decision;
    }

    public Map<String, String> getReturnValues() {
        return returnValues;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public TraceResult getTraceResult() {
        return traceResult;
    }

    public enum Decision {
        PERMIT(0), DENY(1), INDETERMINATE(2), NOT_APPLICABLE(3);

        private final int index;

        Decision(int index) {
            this.index = index;
        }

        public static Decision getByIndex(int index) {
            for (Decision decision : Decision.values()) {
                if (decision.index == index) {
                    return decision;
                }
            }
            return DENY;
        }
    }
}
