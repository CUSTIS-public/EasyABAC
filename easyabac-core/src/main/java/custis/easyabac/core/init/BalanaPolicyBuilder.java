package custis.easyabac.core.init;

import custis.easyabac.core.init.functions.BalanaFunctions;
import custis.easyabac.core.init.functions.BalanaFunctionsFactory;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.*;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import org.wso2.balana.*;
import org.wso2.balana.Rule;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StandardAttributeFactory;
import org.wso2.balana.attr.xacml3.AttributeDesignator;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitPolicyAlg;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitRuleAlg;
import org.wso2.balana.cond.*;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.Function;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.finder.impl.CurrentEnvModule;
import org.wso2.balana.xacml3.*;
import org.wso2.balana.xacml3.Target;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static custis.easyabac.core.init.BalanaModelTransformer.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Converts domain model Policy to Balana PolicySet
 */
class BalanaPolicyBuilder {

    private static final DenyUnlessPermitPolicyAlg DENY_UNLESS_PERMIT_POLICY_ALG = new DenyUnlessPermitPolicyAlg();
    private static final DenyUnlessPermitRuleAlg DENY_UNLESS_PERMIT_RULE_ALG = new DenyUnlessPermitRuleAlg();

    private static final Map<String, AttributeDesignator> predefinedAttributes;

    static {
        predefinedAttributes = new HashMap<>();

        predefinedAttributes.put(AttributesConstants.ENV_TIME,
                new AttributeDesignator(URI.create(DataType.TIME.getXacmlName()),
                        URI.create(CurrentEnvModule.ENVIRONMENT_CURRENT_TIME),
                        false,
                        URI.create(Category.ENV.getXacmlName())));

        AttributeDesignator currentDate = new AttributeDesignator(URI.create(DataType.DATE.getXacmlName()),
                URI.create(CurrentEnvModule.ENVIRONMENT_CURRENT_DATE),
                false,
                URI.create(Category.ENV.getXacmlName()));
        predefinedAttributes.put(AttributesConstants.ENV_DATE, currentDate);
        predefinedAttributes.put(AttributesConstants.ENV_TODAY, currentDate);

        AttributeDesignator currentDateTime = new AttributeDesignator(URI.create(DataType.DATE_TIME.getXacmlName()),
                URI.create(CurrentEnvModule.ENVIRONMENT_CURRENT_DATETIME),
                false,
                URI.create(Category.ENV.getXacmlName()));
        predefinedAttributes.put(AttributesConstants.ENV_DATETIME, currentDateTime);
        predefinedAttributes.put(AttributesConstants.ENV_NOW, currentDateTime);
    }

    PolicySet buildFrom(AbacAuthModel abacAuthModel) {
        return new PolicySet(defaultBalanaPolicySetId(),
                DENY_UNLESS_PERMIT_POLICY_ALG,
                new Target(),
                buildPolicies(abacAuthModel));
    }

    List<org.wso2.balana.Policy> buildPolicies(AbacAuthModel abacAuthModel) {
        return abacAuthModel.getPolicies().stream()
                .map(this::buildBalanaPolicy)
                .collect(toList());
    }

    private org.wso2.balana.Policy buildBalanaPolicy(Policy abacPolicy) {
        return new org.wso2.balana.Policy(balanaPolicyId(abacPolicy.getId()),
                null,
                DENY_UNLESS_PERMIT_RULE_ALG,
                abacPolicy.getTitle(),
                buildBalanaTarget(abacPolicy.getTarget()),
                null,
                buildBalanaRules(abacPolicy.getRules(), abacPolicy.getId()),
                buildBalanaObligations(abacPolicy.getReturnAttributes()));
    }

    private Set<AbstractObligation> buildBalanaObligations(List<Attribute> returnAttributes) {
        return returnAttributes.stream()
                .map(ra -> new ObligationExpression(Result.DECISION_PERMIT,
                        singletonList(
                                new AttributeAssignmentExpression(URI.create(ra.getId()),
                                        URI.create(ra.getCategory().getXacmlName()),
                                        createAttributeDesignator(ra, false), null)),
                        URI.create(ra.getId()))
                ).collect(Collectors.toSet());
    }

    private List<Rule> buildBalanaRules(List<custis.easyabac.core.model.abac.Rule> rules, String policyId) {
        return rules.stream()
                .map(r -> buildBalanaRule(r, policyId))
                .collect(toList());
    }

    private Rule buildBalanaRule(custis.easyabac.core.model.abac.Rule rule, String policyId) {
        return new Rule(balanaRuleId(policyId, rule.getId()),
                rule.getEffect() == Effect.PERMIT ? Result.DECISION_PERMIT : Result.DECISION_DENY,
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
            expression = new Apply(new NotFunction(NotFunction.NAME_NOT), singletonList(expression));
        }

        return new Condition(expression);
    }

    private Apply buildRuleConditionApply(custis.easyabac.core.model.abac.Condition condition) {
        Attribute firstOperand = condition.getFirstOperand();
        final DataType firstOperandType = firstOperand.getType();

        BalanaFunctions bFunc = BalanaFunctionsFactory.getFunctions(firstOperandType);
        custis.easyabac.core.model.abac.Function cf = condition.getFunction();
        final Function function = bFunc.pick(cf);

        final Attribute secondOperandAttribute = condition.getSecondOperandAttribute();
        Expression secondAttr;

        if (secondOperandAttribute != null) {
            secondAttr = createAttributeDesignator(secondOperandAttribute, !bFunc.requiresRightBagAttribute(cf));
        } else {
            final String conditionId = condition.getId();
            if (condition.getSecondOperandValue() != null && !condition.getSecondOperandValue().isEmpty()) {
                if (bFunc.requiresBagOfValues(cf)) {

                    List<AttributeValue> attrValues = condition.getSecondOperandValue().stream()
                            .map(s -> createAttributeValue(s, firstOperandType, conditionId))
                            .collect(toList());

                    secondAttr = new Apply(bFunc.bag(), attrValues);
                } else {
                    if (condition.getSecondOperandValue().size() != 1) {
                        throw new BalanaPolicyBuildException(
                                format("Rule condition id=%s has more than one value", conditionId));
                    }

                    secondAttr = createAttributeValue(condition.getSecondOperandValue().get(0), firstOperandType, conditionId);
                }
            } else {
                throw new BalanaPolicyBuildException(
                        format("Condition %s has neither second operand value nor second operand attribute",
                                conditionId));
            }
        }

        return new Apply(function,
                asList(createAttributeDesignator(firstOperand, !bFunc.requiresLeftBagAttribute(cf)), secondAttr));
    }

    //FIXME Duplicate of BalanaAttributesFactory.getAttributeValue
    private AttributeValue createAttributeValue(String value, DataType dataType, String conditionId) {
        try {
            return StandardAttributeFactory.getFactory().createValue(
                    URI.create(dataType.getXacmlName()),
                    createXACMLAttributeValue(value, dataType));
        } catch (UnknownIdentifierException | ParsingException e) {
            throw new BalanaPolicyBuildException(format(
                    "Failed to create Balana attribute value from [%s] in rule condition id=%s: %s",
                    value, conditionId, e.getMessage()));
        }
    }

    private String createXACMLAttributeValue(String value, DataType dataType) {
        switch (dataType) {
            case TIME:
                return value + ":00"; // XML Schema time format: hh:mm:ss
            default:
                return value;
        }
    }

    private Evaluatable createAttributeDesignator(Attribute attribute, boolean selectAsOneValueFromBag) {
        final AttributeDesignator designator = predefinedAttributes.getOrDefault(attribute.getId(),
                new AttributeDesignator(
                        URI.create(attribute.getType().getXacmlName()),
                        URI.create(attribute.getXacmlName()),
                        true,
                        URI.create(attribute.getCategory().getXacmlName())));
        return selectAsOneValueFromBag ?
                new Apply(BalanaFunctionsFactory.getFunctions(attribute.getType()).oneAndOnly(), singletonList(designator))
                :
                designator;
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

        return new Target(singletonList(new AnyOfSelection(allOfSelections)));
    }

    private List<AllOfSelection> buildDisjunction(List<TargetCondition> conditions) {
        return conditions.stream()
                .map(condition -> {
                    TargetMatch match = buildTargetMatch(condition);
                    return new AllOfSelection(singletonList(match));
                })
                .collect(toList());
    }

    private List<AllOfSelection> buildConjunction(List<TargetCondition> conditions) {
        List<TargetMatch> matches = conditions.stream()
                .map(this::buildTargetMatch)
                .collect(toList());
        return singletonList(new AllOfSelection(matches));
    }

    private TargetMatch buildTargetMatch(TargetCondition targetCondition) {
        final Attribute firstOperand = targetCondition.getFirstOperand();
        BalanaFunctions balanaFunctions = BalanaFunctionsFactory.getFunctions(firstOperand.getType());

        //TODO when functions are 'in', 'oneOf', 'subset' -> create bag attribute
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

        final custis.easyabac.core.model.abac.Function conditionFunction = targetCondition.getFunction();
        return new TargetMatch(balanaFunctions.pick(conditionFunction),
                createAttributeDesignator(firstOperand, false), attributeValue);
    }

    public static void main(String[] args) throws EasyAbacInitException, IOException {
        BalanaPolicyBuilder builder = new BalanaPolicyBuilder();
        PolicySet policySet = builder.buildFrom(AbacAuthModelFactory.getInstance(ModelType.XACML,
                BalanaPolicyBuilder.class.getResourceAsStream("/test.yaml")));
        final FileWriter fileWriter = new FileWriter("test.xacml");
        fileWriter.write(policySet.encode());
        fileWriter.flush();
        fileWriter.close();
    }
}