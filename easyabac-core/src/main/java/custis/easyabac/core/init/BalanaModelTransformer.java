package custis.easyabac.core.init;

import java.net.URI;

public class BalanaModelTransformer {
    private static final String POLICY_NAMESPACE = "urn:oasis:names:tc:xacml:3.0:easy-policy-sample";

    public static URI balanaPolicyId(String id) {
        return URI.create(POLICY_NAMESPACE + ":" + id);
    }

    public static URI balanaRuleId(String policyId, String id) {
        return URI.create(POLICY_NAMESPACE + ":" + policyId + ":" + id);
    }

    public static String policyIdFromBalanaPolicyId(URI balanaId) {
        return balanaId.toString().substring(POLICY_NAMESPACE.length() + 1);
    }

    public static String ruleIdFromBalanaPolicyId(URI balanaId, String policyId) {
        return balanaId.toString().substring(POLICY_NAMESPACE.length() + policyId.length() + 2);
    }
}
