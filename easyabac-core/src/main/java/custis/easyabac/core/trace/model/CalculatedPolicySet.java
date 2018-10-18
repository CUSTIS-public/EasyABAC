package custis.easyabac.core.trace.model;

import custis.easyabac.model.AbacAuthModel;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Trace result
 */
public class CalculatedPolicySet extends AbstractCalculatedPolicy {

    private List<CalculatedPolicySet> innerPolicySets = new ArrayList<>(); // NOT USED in EasyModel
    private List<CalculatedPolicy> policies = new ArrayList<>();

    public CalculatedPolicySet(URI id) {
        super(id);
    }

    public void addPolicy(CalculatedPolicy calculatedPolicy) {
        policies.add(calculatedPolicy);
    }

    public void addInnerPolicySet(CalculatedPolicySet traceResult) {
        innerPolicySets.add(traceResult);
    }

    public List<CalculatedPolicySet> getInnerPolicySets() {
        return innerPolicySets;
    }

    public List<CalculatedPolicy> getPolicies() {
        return policies;
    }

    @Override
    public String toString() {
        return "CalculatedPolicySet{" +
                "innerPolicySets=" + innerPolicySets +
                ", policies=" + policies +
                ", id='" + id + '\'' +
                ", result=" + result +
                ", match=" + match +
                ", combinationResult=" + combinationResult +
                '}';
    }

    public void populateByModel(AbacAuthModel abacAuthModel) {
        for (int i = 0; i < abacAuthModel.getPolicies().size(); i++) {
            if (i > policies.size() - 1) {
                break;
            }
            policies.get(i).populate(abacAuthModel.getPolicies().get(i));
        }
    }
}
