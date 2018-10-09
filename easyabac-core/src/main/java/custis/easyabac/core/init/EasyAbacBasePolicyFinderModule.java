package custis.easyabac.core.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicySet;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Write documentation for EasyAbacBasePolicyFinderModule
 */
public abstract class EasyAbacBasePolicyFinderModule extends PolicyFinderModule {

    protected final static Log log = LogFactory.getLog(FileBasedPolicyFinderModule.class);

    protected Map<URI, org.wso2.balana.Policy> policies;
    protected PolicyCombiningAlgorithm combiningAlg;

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        ArrayList<org.wso2.balana.Policy> selectedPolicies = new ArrayList<>();
        Set<Map.Entry<URI, org.wso2.balana.Policy>> entrySet = policies.entrySet();

        // iterate through all the policies we currently have loaded
        for (Map.Entry<URI, org.wso2.balana.Policy> entry : entrySet) {

            org.wso2.balana.Policy policy = entry.getValue();
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

        if (log.isDebugEnabled()) {
            for (Policy selectedPolicy : selectedPolicies) {
                log.debug(selectedPolicy.encode());
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
