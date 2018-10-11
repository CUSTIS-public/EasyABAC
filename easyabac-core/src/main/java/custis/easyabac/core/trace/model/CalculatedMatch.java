package custis.easyabac.core.trace.model;

import org.wso2.balana.MatchResult;

/**
 * Calculation match for policy
 */
public class CalculatedMatch {
    private final String value;

    public CalculatedMatch(String value) {
        this.value = value;
    }

    public static CalculatedMatch of(int result) {
        return new CalculatedMatch(formatMatchResult(result));
    }

    private static String formatMatchResult(int result) {
        switch (result) {
            case MatchResult.MATCH:
                return "MATCH";
            case MatchResult.INDETERMINATE:
                return "N/A";
            case MatchResult.NO_MATCH:
                return "NO_MATCH";
        }
        return "N/A";
    }
}
