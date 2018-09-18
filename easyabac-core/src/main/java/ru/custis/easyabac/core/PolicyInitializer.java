package ru.custis.easyabac.core;

import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PolicyInitializer {


//    private static void initBalana() {
//
//        try {
//            // using file based policy repository. so set the policy location as system property
//            String policyLocation = (new File(".")).getCanonicalPath() + File.separator + "resources" + File.separator + "policy.xml";
//            System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policyLocation);
//        } catch (IOException e) {
//            System.err.println("Can not locate policy repository");
//        }
//        // create default instance of Balana
//        balana = Balana.getInstance();
//    }

    /**
     * Returns a new PDP instance with new XACML policies
     *
     * @return a  PDP instance
     */
    public PDP getPDPNewInstance(String policyXacml) {

        PolicyFinder policyFinder = new PolicyFinder();

//        Set<String> policyLocations = new HashSet<>();
//        try {
//            String policyPath = (new File(".")).getCanonicalPath() + File.separator +
//                    "src" + File.separator + "test" + File.separator +
//                    "resources" + File.separator + "policy.xml";
//            policyLocations.add(policyPath);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        FileBasedPolicyFinderModule fileBasedPolicyFinderModule = new FileBasedPolicyFinderModule(policyLocations);
        PolicyFinderModule stringPolicyFinderModule = new StringPolicyFinderModule(policyXacml);
        Set<PolicyFinderModule> policyModules = new HashSet<>();

        policyModules.add(stringPolicyFinderModule);
        policyFinder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();

        // registering new attribute finder. so default PDPConfig is needed to change
        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();
        List<AttributeFinderModule> finderModules = attributeFinder.getModules();
        finderModules.add(new SampleAttributeFinderModule());
        attributeFinder.setModules(finderModules);

        return new PDP(new PDPConfig(null, policyFinder, null, true));
    }

}
