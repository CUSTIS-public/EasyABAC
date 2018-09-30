package custis.easyabac;

import custis.easyabac.core.init.EasyPolicyBuilder;
import custis.easyabac.core.model.policy.EasyPolicy;
import org.junit.Before;
import org.junit.Test;
import org.wso2.balana.AbstractPolicy;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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
    public void buildPolicyIdAndDescription() throws URISyntaxException {
        Map<URI, AbstractPolicy> policies = easyPolicyBuilder.buildFrom(easyPolicy);

        AbstractPolicy policy = policies.values().iterator().next();
        assertEquals("Policy ID", policy.getId(), new URI("urn:oasis:names:tc:xacml:3.0:easy-policy-sample:policy2"));
        assertEquals("Policy description", policy.getDescription(), "policy 2 description");
    }
}