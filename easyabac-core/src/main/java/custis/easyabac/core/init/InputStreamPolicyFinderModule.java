package custis.easyabac.core.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.*;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.utils.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputStreamPolicyFinderModule extends PolicyFinderModule {

    private final static Log log = LogFactory.getLog(FileBasedPolicyFinderModule.class);
    private final Map<URI, AbstractPolicy> policies;
    private PolicyCombiningAlgorithm combiningAlg;
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

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        ArrayList<AbstractPolicy> selectedPolicies = new ArrayList<>();
        Set<Map.Entry<URI, AbstractPolicy>> entrySet = policies.entrySet();

        // iterate through all the policies we currently have loaded
        for (Map.Entry<URI, AbstractPolicy> entry : entrySet) {

            AbstractPolicy policy = entry.getValue();
            MatchResult match = policy.match(context);
            int result = match.getResult();

            // if target matching was indeterminate, then return the error
            if (result == MatchResult.INDETERMINATE)
                return new PolicyFinderResult(match.getStatus());

            // see if the target matched
            if (result == MatchResult.MATCH) {

                if ((combiningAlg == null) && (selectedPolicies.size() > 0)) {
                    // we found a match before, so this is an error
                    ArrayList<String> code = new ArrayList<>();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable "
                            + "top-level policies");
                    return new PolicyFinderResult(status);
                }

                // this is the first match we've found, so remember it
                selectedPolicies.add(policy);
            }
        }

        // no errors happened during the search, so now take the right
        // action based on how many policies we found
        switch (selectedPolicies.size()) {
            case 0:
                if (log.isDebugEnabled()) {
                    log.debug("No matching XACML policy found");
                }
                return new PolicyFinderResult();
            case 1:
                return new PolicyFinderResult((selectedPolicies.get(0)));
            default:
                return new PolicyFinderResult(new PolicySet(null, combiningAlg, null, selectedPolicies));
        }
    }
}

