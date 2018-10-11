package custis.easyabac.core.trace.model;

import java.util.List;

/**
 * Trace result
 */
public class TraceResult {

    public static final TraceResult EMPTY = new TraceResult();

    private CalculatedResult result;
    private List<CalculatedPolicySet> sets;
}
