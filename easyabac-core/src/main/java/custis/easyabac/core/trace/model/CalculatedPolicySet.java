package custis.easyabac.core.trace.model;

import java.util.List;

/**
 * Policy with trace
 */
public class CalculatedPolicySet {

    private CalculatedResult result;
    private List<CalculatedPolicy> policies;
    private List<CalculatedPolicySet> sets;
}
