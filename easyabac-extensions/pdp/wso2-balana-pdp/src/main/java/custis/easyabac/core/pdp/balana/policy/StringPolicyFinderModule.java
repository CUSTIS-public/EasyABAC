package custis.easyabac.core.pdp.balana.policy;

import java.io.ByteArrayInputStream;

public class StringPolicyFinderModule extends InputStreamPolicyFinderModule {

    public StringPolicyFinderModule(String policyXacml, boolean useProxy) {
        super(new ByteArrayInputStream(policyXacml.getBytes()), useProxy);
    }

}

