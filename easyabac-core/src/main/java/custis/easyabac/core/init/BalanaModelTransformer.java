package custis.easyabac.core.init;

import java.net.URI;

public class BalanaModelTransformer {
    private static final String POLICY_NAMESPACE = "urn:oasis:names:tc:xacml:3.0:easy-policy";
    private static final String DEFAULT_POLICY_SET_ID = "urn:oasis:names:tc:xacml:3.0:easy-policy:policy-set";

    public static URI balanaPolicyId(String id) {
        return URI.create(POLICY_NAMESPACE + ":" + id);
    }
    public static URI defaultBalanaPolicySetId() {
        return URI.create(DEFAULT_POLICY_SET_ID);
    }

    public static URI balanaRuleId(String policyId, String id) {
        return URI.create(POLICY_NAMESPACE + ":" + simpleRuleId(policyId, id));
    }

    public static String clearBalanaNamespace(URI balanaId) {
        return balanaId.toString().substring(POLICY_NAMESPACE.length() + 1);
    }

    public static String simpleRuleId(String policyId, String id) {
        return policyId + ":" + id;
    }

}
