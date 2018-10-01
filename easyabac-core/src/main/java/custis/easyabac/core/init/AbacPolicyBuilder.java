package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Operation;
import custis.easyabac.core.model.abac.TargetCondition;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.TargetMatch;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitRuleAlg;
import org.wso2.balana.xacml3.AllOfSelection;
import org.wso2.balana.xacml3.AnyOfSelection;
import org.wso2.balana.xacml3.Target;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Converts EasyPolicy to Balana policies
 */
public class AbacPolicyBuilder {

    private static final String POLICY_NAMESPACE = "policy.namespace";

    private String policyNamespace;

    public AbacPolicyBuilder(Properties properties) {
        this.policyNamespace = properties.getProperty(POLICY_NAMESPACE);
    }

    public Map<URI, org.wso2.balana.Policy> buildFrom(AbacAuthModel abacAuthModel) {
        return abacAuthModel.getPolicies().stream()
                .map(this::buildBalanaPolicy)
                .collect(Collectors.toMap(AbstractPolicy::getId, bp -> bp));
    }

    private org.wso2.balana.Policy buildBalanaPolicy(Policy abacPolicy) {
        return new org.wso2.balana.Policy(URI.create(policyNamespace + ":" + abacPolicy.getId()),
                null,
                new DenyUnlessPermitRuleAlg(),
                abacPolicy.getTitle(),
                buildTarget(abacPolicy.getTarget()),
                Collections.emptyList());
    }

    private Target buildTarget(custis.easyabac.core.model.abac.Target target) {

        List<AllOfSelection> allOfSelections;

        final List<TargetCondition> conditions = target.getConditions();
        if (target.getOperation() == Operation.OR) {
            allOfSelections = makeDisjunction(conditions);
        } else if (target.getOperation() == Operation.AND) {
            allOfSelections = makeConjunction(conditions);
        } else {
            throw new EasyPolicyBuildException("Unsupported target operation: " + target.getOperation());
        }

        return new Target(Collections.singletonList(new AnyOfSelection(allOfSelections)));
    }

    private List<AllOfSelection> makeDisjunction(List<TargetCondition> conditions) {
        return conditions.stream()
                .map(condition -> {
                    TargetMatch match = makeTargetMatch(condition);
                    return new AllOfSelection(Collections.singletonList(match));
                })
                .collect(Collectors.toList());
    }

    private List<AllOfSelection> makeConjunction(List<TargetCondition> conditions) {
        List<TargetMatch> matches = conditions.stream()
                .map(this::makeTargetMatch)
                .collect(Collectors.toList());
        return Collections.singletonList(new AllOfSelection(matches));
    }

    private TargetMatch makeTargetMatch(TargetCondition targetCondition) {
//        TargetMatch match = new TargetMatch()
        return null;
    }

}