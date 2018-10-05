package custis.easyabac.core.init;

import custis.easyabac.core.init.functions.BalanaFunctions;
import custis.easyabac.core.init.functions.BalanaFunctionsFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Operation;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.TargetCondition;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.DataType;
import org.wso2.balana.*;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StandardAttributeFactory;
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

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

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
                .collect(toList());
    }

    private Rule buildBalanaRule(custis.easyabac.core.model.abac.Rule rule, String policyId) {
        return new Rule(URI.create(policyNamespace + ":" + policyId + ":" + rule.getId()),
                Result.DECISION_PERMIT,
                rule.getTitle(),
                null,
                buildRuleCondition(rule.getConditions(), rule.getOperation()),
                null,
                null,
                XACMLConstants.XACML_VERSION_3_0
                );
    }

    private Condition buildRuleCondition(List<custis.easyabac.core.model.abac.Condition> conditions,
                                         Operation operation) {
        Function applyFunction;
        boolean negate = false;
        switch (operation) {
            case AND:
                applyFunction = new LogicalFunction(LogicalFunction.NAME_AND);
                break;
            case OR:
                applyFunction = new LogicalFunction(LogicalFunction.NAME_OR);
                break;
            case NAND:
                applyFunction = new LogicalFunction(LogicalFunction.NAME_AND);
                negate = true;
                break;
            case NOR:
                applyFunction = new LogicalFunction(LogicalFunction.NAME_OR);
                negate = true;
                break;
            default:
                throw new BalanaPolicyBuildException("Unsupported condition operation: " + operation);
        }

        List<Apply> operands = conditions.stream()
                .map(this::buildRuleConditionApply)
                .collect(toList());

        Apply expression = new Apply(applyFunction, operands);
        if (negate) {
            expression = new Apply(new NotFunction(NotFunction.NAME_NOT), Collections.singletonList(expression));
        }

        return new Condition(expression);
    }

    private Apply buildRuleConditionApply(custis.easyabac.core.model.abac.Condition condition) {
        Attribute firstOperand = condition.getFirstOperand();
        BalanaFunctions balanaFunctions = BalanaFunctionsFactory.getFunctions(firstOperand.getType());

        Expression secondAttr = null;
        final Attribute secondOperandAttribute = condition.getSecondOperandAttribute();

        if (secondOperandAttribute != null) {
            secondAttr = createAttributeDesignator(secondOperandAttribute);
        } else if (condition.getSecondOperandValue() != null && !condition.getSecondOperandValue().isEmpty()) {
            if (balanaFunctions.requiresBagAttribute(condition.getFunction())) {

            } else {
                if (condition.getSecondOperandValue().size() != 1) {
                    throw new BalanaPolicyBuildException(
                            format("Rule condition id=%s has more than one value", condition.getId()));
                }

                final String value = condition.getSecondOperandValue().get(0);
                try {
                    secondAttr = StandardAttributeFactory.getFactory().createValue(
                            URI.create(firstOperand.getType().getXacmlName()),
                            value);
                } catch (UnknownIdentifierException | ParsingException e) {
                    throw new BalanaPolicyBuildException(format(
                            "Failed to create Balana attribute value from [%s] in rule condition id=%s: %s",
                            value, condition.getId(), e.getMessage()));
                }
            }
        } else {
            throw new BalanaPolicyBuildException(
                    format("Condition %s has neither second operand value nor second operand attribute",
                            condition.getId()));
        }

        return new Apply(balanaFunctions.pick(condition.getFunction()),
                asList(createAttributeDesignator(firstOperand), secondAttr));
    }

    private AttributeDesignator createAttributeDesignator(Attribute firstOperand) {
        return new AttributeDesignator(
                URI.create(firstOperand.getType().getXacmlName()),
                URI.create(firstOperand.getId()),
                true,
                URI.create(firstOperand.getCategory().getXacmlName()));
    }

    private Target buildBalanaTarget(custis.easyabac.core.model.abac.Target target) {

        List<AllOfSelection> allOfSelections;

        final List<TargetCondition> conditions = target.getConditions();
        Operation targetOperation = target.getOperation() == null ? Operation.OR : target.getOperation();
        if (targetOperation == Operation.OR) {
            allOfSelections = buildDisjunction(conditions);
        } else if (targetOperation == Operation.AND) {
            allOfSelections = buildConjunction(conditions);
        } else {
            throw new BalanaPolicyBuildException("Unsupported target operation: " + targetOperation);
        }

        return new Target(Collections.singletonList(new AnyOfSelection(allOfSelections)));
    }

    private List<AllOfSelection> buildDisjunction(List<TargetCondition> conditions) {
        return conditions.stream()
                .map(condition -> {
                    TargetMatch match = buildTargetMatch(condition);
                    return new AllOfSelection(Collections.singletonList(match));
                })
                .collect(toList());
    }

    private List<AllOfSelection> buildConjunction(List<TargetCondition> conditions) {
        List<TargetMatch> matches = conditions.stream()
                .map(this::buildTargetMatch)
                .collect(toList());
        return Collections.singletonList(new AllOfSelection(matches));
    }

    private TargetMatch buildTargetMatch(TargetCondition targetCondition) {
        final Attribute firstOperand = targetCondition.getFirstOperand();
        BalanaFunctions balanaFunctions = BalanaFunctionsFactory.getFunctions(firstOperand.getType());

        //TODO when functions are 'in', 'oneOf', 'subset' -> create complex attribute
        final DataType dataType = targetCondition.getFirstOperand().getType();
        AttributeValue attributeValue;
        try {
            attributeValue = StandardAttributeFactory.getFactory().createValue(
                     URI.create(dataType.getXacmlName()),
                     targetCondition.getSecondOperand());

        } catch (UnknownIdentifierException | ParsingException e) {
            throw new BalanaPolicyBuildException(format(
                    "Failed to create Balana attribute value from [%s] in target condition id=%s: %s",
                    targetCondition.getSecondOperand(), targetCondition.getId(), e.getMessage()));
        }

        return new TargetMatch(balanaFunctions.pick(targetCondition.getFunction()),
                createAttributeDesignator(firstOperand), attributeValue);
    }
}