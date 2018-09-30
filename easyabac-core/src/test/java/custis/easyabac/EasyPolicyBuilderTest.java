package custis.easyabac;

import custis.easyabac.core.init.EasyPolicyBuilder;
import custis.easyabac.core.model.policy.EasyPolicy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.balana.AbstractPolicy;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

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

        this.easyPolicyBuilder = new EasyPolicyBuilder();
    }

    @Test
    public void createPolicy_whenEasyPolicyProvided() {
        Map<URI, AbstractPolicy> policies = easyPolicyBuilder.buildFrom(easyPolicy);
        Assert.assertNotNull("Policies map is not null", policies);
        Assert.assertEquals("Policy qty", 1, policies.size());
    }
}