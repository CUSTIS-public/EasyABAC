package custis.easyabac.core.init;

import custis.easyabac.core.model.policy.EasyPolicy;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.Policy;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitRuleAlg;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Converts EasyPolicy to Balana policies
 */
public class EasyPolicyBuilder {

    private static final String POLICY_NAMESPACE = "policy.namespace";

    private String policyNamespace;

    public EasyPolicyBuilder(Properties properties) {
        this.policyNamespace = properties.getProperty(POLICY_NAMESPACE);
    }

    public Map<URI, AbstractPolicy> buildFrom(EasyPolicy easyPolicy) {
        return easyPolicy.getPolicies().entrySet().stream()
                .map(e -> buildBalanaPolicy(e.getValue(), e.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AbstractPolicy::getId, bp -> bp));
    }

    private AbstractPolicy buildBalanaPolicy(custis.easyabac.core.model.policy.Policy easyPolicy, String policyKey) {
        try {

            return new Policy(URI.create(policyNamespace + ":" + policyKey),
                    null,
                    new DenyUnlessPermitRuleAlg(new URI("")),
                    easyPolicy.getTitle(),
                    null,
                    Collections.emptyList());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}