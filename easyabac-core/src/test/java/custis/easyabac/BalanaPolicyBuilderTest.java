package custis.easyabac;

import custis.easyabac.core.init.BalanaPolicyBuilder;
import custis.easyabac.core.model.abac.*;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.TargetMatch;
import org.wso2.balana.attr.xacml3.AttributeDesignator;
import org.wso2.balana.cond.Apply;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.xacml3.AllOfSelection;
import org.wso2.balana.xacml3.AnyOfSelection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

/**
 * Test suite for conversion from EasyAbac domain model to Balana domain model
 */
public class BalanaPolicyBuilderTest {

    private AbacAuthModel authModel;
    private BalanaPolicyBuilder balanaPolicyBuilder;

    @Before
    public void buildPolicy() {
        this.authModel = buildAbacAuthModel();
        try {
            Properties builderProperties = new Properties();
            builderProperties.load(getClass().getResourceAsStream("/easy-policy-builder.properties"));
            this.balanaPolicyBuilder = new BalanaPolicyBuilder(builderProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AbacAuthModel buildAbacAuthModel() {
        Target target = new Target(Operation.OR, asList(
                new TargetCondition(Attribute.ACTION_ID, "CourseUnit.Edit", Function.EQUAL),
                new TargetCondition(Attribute.ACTION_ID, "CourseUnit.Delete", Function.EQUAL)),
                emptyList());

        Rule rule1 = new Rule("rule1", "First rule", Operation.AND, asList(
                new Condition("r1c1", false,
                        new Attribute("CourseUnit.authorId", DataType.STRING, Category.RESOURCE, false),
                        new Attribute("user.personId", DataType.STRING, Category.SUBJECT, false),
                        Function.EQUAL),
                new Condition("r1c2", true,
                        new Attribute("CourseUnit.status", DataType.STRING, Category.RESOURCE, false),
                        singletonList("DRAFT"),
                        Function.EQUAL)));

        Rule rule2 = new Rule("rule2", "Second rule", Operation.NAND, asList(
                new Condition("r2c1", false,
                        new Attribute("CourseUnit.curatorId", DataType.STRING, Category.RESOURCE, false),
                        new Attribute("user.personId", DataType.STRING, Category.SUBJECT, false),
                        Function.EQUAL),
                new Condition("r2c2", false,
                        new Attribute("CourseUnit.count", DataType.INT, Category.RESOURCE, false),
                        singletonList("15"),
                        Function.GREATER),
                new Condition("r2c3", false,
                        new Attribute("CourseUnit.status", DataType.STRING, Category.RESOURCE, false),
                        asList("DRAFT", "TEST"),
                        Function.IN)));

        Policy abacPolicy = new Policy("policy1", "Sample policy", target, asList(rule1, rule2), emptyList());

        return new AbacAuthModel(singletonList(abacPolicy), Collections.emptyMap(), Collections.emptyMap());
    }

    @Test
    public void createPolicy_whenEasyPolicyProvided() {
        Map<URI, org.wso2.balana.Policy> policies = balanaPolicyBuilder.buildFrom(authModel);
        assertNotNull("Policies map is not null", policies);
        assertEquals("Policy qty", 1, policies.size());
    }

    @Test
    public void buildPolicyId_fromPolicyKeyAndBuilderSettings() throws URISyntaxException {
        AbstractPolicy policy = pickSinglePolicy();
        assertEquals("Policy ID",
                new URI("urn:oasis:names:tc:xacml:3.0:easy-policy-sample:policy1"),
                policy.getId());
    }

    @Test
    public void buildPolicyDescription_whenTitleProvided() {
        AbstractPolicy policy = pickSinglePolicy();
        assertEquals("Policy description", policy.getDescription(), "Sample policy");
    }

    @Test
    @Ignore
    public void buildPolicyTarget_whenSpecifiedInEasyPolicy() {
        org.wso2.balana.Policy policy = pickSinglePolicy();

        final org.wso2.balana.xacml3.Target target = (org.wso2.balana.xacml3.Target) policy.getTarget();
        assertNotNull("Policy target", target);

        assertEquals("Policy target anyOf size", 1, target.getAnyOfSelections().size());
        final AnyOfSelection anyOfSelection = target.getAnyOfSelections().iterator().next();
        final List<AllOfSelection> allOfSelections = anyOfSelection.getAllOfSelections();
        assertEquals("Policy target allOf size", 2, allOfSelections.size());

        AllOfSelection actionSelection = allOfSelections.get(0);
        assertEquals("Match qty", 1, actionSelection.getMatches().size());

        TargetMatch targetMatch = actionSelection.getMatches().get(0);
        assertEquals("Match function",
                "urn:oasis:names:tc:xacml:1.0:function:string-equal",
                targetMatch.getMatchFunction().getIdentifier().toString());

        assertEquals("Match value", "CourseUnit.Edit", targetMatch.getMatchValue().encode());
        Evaluatable matchEvaluatable = targetMatch.getMatchEvaluatable();
        assertTrue("Match evaluatable is Apply", matchEvaluatable instanceof Apply);
        Apply apply = (Apply) matchEvaluatable;
        boolean hasAttrDesignator = apply.getChildren().stream()
                .anyMatch(c -> {
                     if (!(c instanceof AttributeDesignator)) {
                         return false;
                     }
                     AttributeDesignator ad = (AttributeDesignator) c;

                     return "urn:oasis:names:tc:xacml:3.0:attribute-category:action".equals(ad.getCategory().toString());
                });

        assertTrue("Match has action attribute designator", hasAttrDesignator);
    }

    @Test
    public void buildRules_whenGiven() {
        org.wso2.balana.Policy policy = pickSinglePolicy();

        final List<org.wso2.balana.Rule> balanaRules = extractRules(policy);
        assertEquals("Rules count", 2, balanaRules.size());
    }

    @Test
    public void buildRuleConditionAsSingleApply_whenGiven() {
        org.wso2.balana.Policy policy = pickSinglePolicy();
        final List<org.wso2.balana.Rule> balanaRules = extractRules(policy);

        org.wso2.balana.Rule rule1 = balanaRules.get(0);
        final org.wso2.balana.cond.Condition condition = rule1.getCondition();
        assertNotNull("Rule 1 condition", condition);

        assertEquals("Rule 1 condition children qty", 1, condition.getChildren().size());
        final Object condChild = condition.getChildren().iterator().next();
        assertTrue("Single condition child is Apply", condChild instanceof Apply);

        Apply andApply = (Apply) condChild;
        assertEquals("Condition apply function",
                URI.create("urn:oasis:names:tc:xacml:1.0:function:and"),
                andApply.getFunction().getIdentifier());

        assertEquals("Apply's children qty", 2, andApply.getChildren().size());
    }

    private org.wso2.balana.Policy pickSinglePolicy() {
        Map<URI, org.wso2.balana.Policy> policies = balanaPolicyBuilder.buildFrom(authModel);

        return policies.values().iterator().next();
    }

    private List<org.wso2.balana.Rule> extractRules(org.wso2.balana.Policy policy) {
        return policy.getChildren().stream()
                .filter(e -> e instanceof org.wso2.balana.Rule)
                .map(e -> (org.wso2.balana.Rule) e)
                .collect(Collectors.toList());
    }
}