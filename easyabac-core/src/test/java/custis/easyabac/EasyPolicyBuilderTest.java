package custis.easyabac;

import custis.easyabac.core.init.EasyPolicyBuilder;
import custis.easyabac.core.model.policy.EasyPolicy;
import org.junit.Before;
import org.junit.Test;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.TargetMatch;
import org.wso2.balana.xacml3.AllOfSelection;
import org.wso2.balana.xacml3.AnyOfSelection;
import org.wso2.balana.xacml3.Target;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for conversion from EasyAbac domain model to Balana domain model
 */
public class EasyPolicyBuilderTest {

    private EasyPolicy easyPolicy;

    private EasyPolicyBuilder easyPolicyBuilder;

    @Before
    public void readPolicyFromYAML() {
        Yaml yaml = new Yaml();
        final InputStream policyResource = getClass().getResourceAsStream("/easy-policy1.yaml");
        this.easyPolicy = yaml.loadAs(new InputStreamReader(policyResource), EasyPolicy.class);

        try {
            Properties builderProperties = new Properties();
            builderProperties.load(getClass().getResourceAsStream("/easy-policy-builder.properties"));
            this.easyPolicyBuilder = new EasyPolicyBuilder(builderProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createPolicy_whenEasyPolicyProvided() {
        Map<URI, AbstractPolicy> policies = easyPolicyBuilder.buildFrom(easyPolicy);
        assertNotNull("Policies map is not null", policies);
        assertEquals("Policy qty", 1, policies.size());
    }

    @Test
    public void buildPolicyId_fromPolicyKeyAndBuilderSettings() throws URISyntaxException {
        AbstractPolicy policy = pickSinglePolicy();
        assertEquals("Policy ID", policy.getId(), new URI("urn:oasis:names:tc:xacml:3.0:easy-policy-sample:policy2"));
    }

    @Test
    public void buildPolicyDescription_whenTitleProvided() {
        AbstractPolicy policy = pickSinglePolicy();
        assertEquals("Policy description", policy.getDescription(), "policy 2 description");
    }

    @Test
    public void buildPolicyTarget_whenSpecifiedInEasyPolicy() {
        AbstractPolicy policy = pickSinglePolicy();

        final Target target = (Target) policy.getTarget();
        assertNotNull("Policy target", target);

        assertEquals("Policy target anyOf size", 1, target.getAnyOfSelections().size());
        final AnyOfSelection anyOfSelection = target.getAnyOfSelections().iterator().next();
        final List<AllOfSelection> allOfSelections = anyOfSelection.getAllOfSelections();
        assertEquals("Policy target allOf size", 2, allOfSelections.size());
    }

    private AbstractPolicy pickSinglePolicy() {
        Map<URI, AbstractPolicy> policies = easyPolicyBuilder.buildFrom(easyPolicy);

        return policies.values().iterator().next();
    }
}