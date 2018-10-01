package custis.easyabac;

import custis.easyabac.core.init.AbacPolicyBuilder;
import custis.easyabac.core.model.abac.*;
import org.junit.Before;
import org.junit.Test;
import org.wso2.balana.AbstractPolicy;
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
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for conversion from EasyAbac domain model to Balana domain model
 */
public class AbacPolicyBuilderTest {

    private AbacAuthModel authModel;
    private AbacPolicyBuilder abacPolicyBuilder;

    @Before
    public void buildPolicy() {
        this.authModel = buildAbacAuthModel();
        try {
            Properties builderProperties = new Properties();
            builderProperties.load(getClass().getResourceAsStream("/easy-policy-builder.properties"));
            this.abacPolicyBuilder = new AbacPolicyBuilder(builderProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AbacAuthModel buildAbacAuthModel() {
        Policy abacPolicy = new Policy();
        abacPolicy.setId("policy1");
        abacPolicy.setTitle("Sample policy");

        Target t = new Target();
        TargetCondition tc1 = new TargetCondition("action-id == CourseUnit.Edit");
        TargetCondition tc2 = new TargetCondition("action-id == CourseUnit.Delete");
        t.setConditions(asList(tc1, tc2));
        t.setOperation(Operation.OR);

        abacPolicy.setTarget(t);

        Rule rule1 = new Rule();
        rule1.setId("rule1");
        rule1.setOperation(Operation.AND);
        rule1.setTitle("First rule");
        rule1.setConditions(asList(
                new Condition("object.CourseUnit.authorId in user.personId"),
                new Condition("not object.CourseUnit.status == 'DRAFT'")));

        Rule rule2 = new Rule();
        rule2.setId("rule2");
        rule2.setOperation(Operation.NAND);
        rule2.setTitle("Second rule");
        rule2.setConditions(asList(
                new Condition("object.CourseUnit.curatorId == user.personId"),
                new Condition("object.CourseUnit.count > 15"),
                new Condition("object.CourseUnit.status == ['DRAFT', 'TEST']")));

        abacPolicy.setRules(Stream.of(rule1, rule2).collect(Collectors.toMap(Rule::getId, r -> r)));
        return new AbacAuthModel(singletonList(abacPolicy), Collections.emptyMap(), Collections.emptyList());
    }

    @Test
    public void createPolicy_whenEasyPolicyProvided() {
        Map<URI, org.wso2.balana.Policy> policies = abacPolicyBuilder.buildFrom(authModel);
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
    public void buildPolicyTarget_whenSpecifiedInEasyPolicy() {
        org.wso2.balana.Policy policy = pickSinglePolicy();

        final org.wso2.balana.xacml3.Target target = (org.wso2.balana.xacml3.Target) policy.getTarget();
        assertNotNull("Policy target", target);

        assertEquals("Policy target anyOf size", 1, target.getAnyOfSelections().size());
        final AnyOfSelection anyOfSelection = target.getAnyOfSelections().iterator().next();
        final List<AllOfSelection> allOfSelections = anyOfSelection.getAllOfSelections();
        assertEquals("Policy target allOf size", 2, allOfSelections.size());
    }

    private org.wso2.balana.Policy pickSinglePolicy() {
        Map<URI, org.wso2.balana.Policy> policies = abacPolicyBuilder.buildFrom(authModel);

        return policies.values().iterator().next();
    }
}