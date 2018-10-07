package custis.easyabac.generation.util.algorithm;

public class CombinationAlgorithmFactory {

    public static TestGenerationAlgorithm createByCode(String combiningAlgorithm) {
        if ("deny-unless-permit".equals(combiningAlgorithm)) {
            return new DenyUnlessPermit();
        }
        return null;
    }

}
