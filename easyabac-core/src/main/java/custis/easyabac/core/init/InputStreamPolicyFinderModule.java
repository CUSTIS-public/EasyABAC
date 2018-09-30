package custis.easyabac.core.init;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicySet;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitPolicyAlg;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.utils.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class InputStreamPolicyFinderModule extends EasyAbacBasePolicyFinderModule {

    private final InputStream policyXacmlStream;

    public InputStreamPolicyFinderModule(InputStream policyXacmlStream) {
        policies = new HashMap<>();
        this.policyXacmlStream = policyXacmlStream;
    }

    @Override
    public void init(PolicyFinder finder) {
        loadPolicy(finder);
        combiningAlg = new DenyUnlessPermitPolicyAlg();
    }


    private void loadPolicy(PolicyFinder finder) {
        AbstractPolicy policy = null;

        try {
            // create the factory
            DocumentBuilderFactory factory = Utils.getSecuredDocumentBuilderFactory();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            factory.setValidating(false);

            // create a builder based on the factory & try to load the policy
            DocumentBuilder db = factory.newDocumentBuilder();

            Document doc = db.parse(policyXacmlStream);

            // handle the policy, if it's a known type
            Element root = doc.getDocumentElement();
            String name = DOMHelper.getLocalName(root);

            if (name.equals("Policy")) {
                policy = Policy.getInstance(root);
            } else if (name.equals("PolicySet")) {
                policy = PolicySet.getInstance(root, finder);
            }
        } catch (Exception e) {
            // just only logs
            log.error("Fail to load policy : " + policyXacmlStream, e);
        } finally {
            if (policyXacmlStream != null) {
                try {
                    policyXacmlStream.close();
                } catch (IOException e) {
                    log.error("Error while closing input stream");
                }
            }
        }

        if (policy != null) {
            policies.put(policy.getId(), policy);
        }

    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

}

