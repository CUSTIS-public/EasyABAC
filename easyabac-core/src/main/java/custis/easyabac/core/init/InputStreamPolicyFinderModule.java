package custis.easyabac.core.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicySet;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.utils.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static custis.easyabac.core.init.BalanaModelTransformer.defaultBalanaPolicySetId;
import static custis.easyabac.core.trace.interceptors.cglib.CGLibPolicyElementsFactory.createAbstractPolicy;

public class InputStreamPolicyFinderModule extends PolicyFinderModule {

    private final static Log log = LogFactory.getLog(InputStreamPolicyFinderModule.class);
    private final static PolicyCombiningAlgorithm DENY_UNLESS_PERMIT_POLICY_ALG = new DenyUnlessPermitPolicyAlg();


    private final boolean useProxy;
    private final InputStream policyXacmlStream;
    private AbstractPolicy policySet = null;
    private PolicyFinderResult policyFinderResult = null;

    public InputStreamPolicyFinderModule(InputStream policyXacmlStream, boolean useProxy) {
        this.policyXacmlStream = policyXacmlStream;
        this.useProxy = useProxy;
    }

    @Override
    public void init(PolicyFinder finder) {
        policySet = loadPolicy(policyXacmlStream, finder);
        if (useProxy) {
            this.policySet = createAbstractPolicy(policySet, finder);
        }
        this.policyFinderResult = new PolicyFinderResult(policySet);
    }


    @Override
    public boolean isRequestSupported() {
        return true;
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        return policyFinderResult;
    }

    private static AbstractPolicy loadPolicy(InputStream policyXacmlStream, PolicyFinder finder) {
        AbstractPolicy policySet = null;

        try {
            // create the factory
            DocumentBuilderFactory factory = Utils.getSecuredDocumentBuilderFactory();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            factory.setValidating(false);

            // create a builder based on the factory & try to dto the abac
            DocumentBuilder db = factory.newDocumentBuilder();

            Document doc = db.parse(policyXacmlStream);

            // handle the abac, if it's a known type
            Element root = doc.getDocumentElement();
            String name = DOMHelper.getLocalName(root);

            if (name.equals("Policy")) {
                Policy simplePolicy = Policy.getInstance(root);
                policySet = new PolicySet(defaultBalanaPolicySetId(), DENY_UNLESS_PERMIT_POLICY_ALG, null, Collections.singletonList(simplePolicy));
            } else if (name.equals("PolicySet")) {
                policySet = PolicySet.getInstance(root, finder);
            }
        } catch (Exception e) {
            // just only logs
            log.error("Fail to dto abac : " + policyXacmlStream, e);
        } finally {
            if (policyXacmlStream != null) {
                try {
                    policyXacmlStream.close();
                } catch (IOException e) {
                    log.error("Error while closing input stream");
                }
            }
        }

        if (policySet != null) {
            return policySet;
        }

        return policySet;
    }

}

