package custis.easyabac.generation.algorithm;

public class CombinationAlgorithmFactory {

    private static DenyUnlessPermit denyUnlessPermit = new DenyUnlessPermit();

    public static TestGenerationAlgorithm getByCode(String combiningAlgorithm) {
        if ("deny-unless-permit".equals(combiningAlgorithm)) {
            return denyUnlessPermit;
        }
        return null;
    }

}
