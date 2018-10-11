package custis.easyabac.core.trace.model;

import custis.easyabac.pdp.RequestId;

/**
 * Trace result
 */
public class TraceResult {

    public static final TraceResult EMPTY = new TraceResult(null);

    private RequestId requestId;
    private CalculatedPolicySet mainPolicy;

    public TraceResult(RequestId requestId) {
        this.requestId = requestId;
    }

    public void setMainPolicy(CalculatedPolicySet mainPolicy) {
        this.mainPolicy = mainPolicy;
    }

}
