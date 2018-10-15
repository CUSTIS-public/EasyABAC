package custis.easyabac.core.init;

import java.io.ByteArrayInputStream;

public class StringPolicyFinderModule extends InputStreamPolicyFinderModule {

    public StringPolicyFinderModule(String policyXacml, boolean useProxy) {
        super(new ByteArrayInputStream(policyXacml.getBytes()), useProxy);
    }

}

