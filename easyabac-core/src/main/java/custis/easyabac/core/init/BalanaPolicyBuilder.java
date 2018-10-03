package custis.easyabac.core.init;

import custis.easyabac.core.init.functions.BalanaFunctions;
import custis.easyabac.core.init.functions.BalanaFunctionsFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Operation;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.TargetCondition;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.Rule;
import org.wso2.balana.TargetMatch;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.xacml3.AttributeDesignator;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitRuleAlg;
import org.wso2.balana.cond.*;
import org.wso2.balana.ctx.xacml3.Result;
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
 * Converts domain model Policy to Balana policies
 */
public class BalanaPolicyBuilder {

    private static final String POLICY_NAMESPACE = "policy.namespace";

    //TODO create default app namespace instead of configured one
    private String policyNamespace;

    public BalanaPolicyBuilder(Properties properties) {
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
                buildBalanaTarget(abacPolicy.getTarget()),
                buildBalanaRules(abacPolicy.getRules(), abacPolicy.getId()));
    }

    private List<Rule> buildBalanaRules(List<custis.easyabac.core.model.abac.Rule> rules, String policyId) {
        return rules.stream()
                .map(r -> buildBalanaRule(r, policyId))
                .collect(Collectors.toList());
    }

    private Rule buildBalanaRule(custis.easyabac.core.model.abac.Rule rule, String policyId) {
        return new Rule(URI.create(policyNamespace + ":" + policyId + ":" + rule.getId()),
                Result.DECISION_PERMIT,
                rule.getTitle(),
                null,
                buildBalanaCondition(rule.getConditions(), rule.getOperation()),
                null,
                null,
                XACMLConstants.XACML_VERSION_3_0
                );
    }

    private Condition buildBalanaCondition(List<custis.easyabac.core.model.abac.Condition> conditions,
                                           Operation operation) {
        //TODO implement
        return new Condition(new ComparisonFunction("urn:oasis:names:tc:xacml:1.0:function:integer-greater-than"));
    }

    private Target buildBalanaTarget(custis.easyabac.core.model.abac.Target target) {

        List<AllOfSelection> allOfSelections;

        final List<TargetCondition> conditions = target.getConditions();
        Operation targetOperation = target.getOperation() == null ? Operation.OR : target.getOperation();
        if (targetOperation == Operation.OR) {
            allOfSelections = makeDisjunction(conditions);
        } else if (targetOperation == Operation.AND) {
            allOfSelections = makeConjunction(conditions);
        } else {
            throw new EasyPolicyBuildException("Unsupported target operation: " + targetOperation);
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
        //TODO implement
        BalanaFunctions balanaFunctions = BalanaFunctionsFactory.getFunctions(targetCondition.getFirstOperand().getType());
//        AttributeDesignator attributeDesignator = new AttributeDesignator();
        TargetMatch match = new TargetMatch(balanaFunctions.pick(targetCondition.getFunction()), null, null);
        return match;
    }
}