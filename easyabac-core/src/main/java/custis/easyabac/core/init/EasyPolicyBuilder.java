package custis.easyabac.core.init;

import custis.easyabac.core.model.policy.EasyPolicy;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.Policy;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitRuleAlg;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Converts EasyPolicy to Balana policies
 */
public class EasyPolicyBuilder {

    public Map<URI, AbstractPolicy> buildFrom(EasyPolicy easyPolicy) {
        return easyPolicy.getPolicies().values().stream()
                .map(this::buildBalanaPolicy)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AbstractPolicy::getId, bp -> bp));
    }

    private AbstractPolicy buildBalanaPolicy(custis.easyabac.core.model.policy.Policy easyPolicy) {
        try {
            //TODO build URIs and other policy attributes
            return new Policy(new URI(""), new DenyUnlessPermitRuleAlg(new URI("")), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}