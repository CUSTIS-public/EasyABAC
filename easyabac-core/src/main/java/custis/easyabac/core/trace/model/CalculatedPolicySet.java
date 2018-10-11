package custis.easyabac.core.trace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Trace result
 */
public class CalculatedPolicySet extends AbstractCalculatedPolicy {

    private List<CalculatedPolicySet> innerResults = new ArrayList<>(); // NOT USED in EasyModel
    private List<CalculatedPolicy> policies = new ArrayList<>();

    public CalculatedPolicySet(String id) {
        super(id);
    }

    public void addPolicy(CalculatedPolicy calculatedPolicy) {
        policies.add(calculatedPolicy);
    }

    public void addInnerResult(CalculatedPolicySet traceResult) {
        innerResults.add(traceResult);
    }

}
