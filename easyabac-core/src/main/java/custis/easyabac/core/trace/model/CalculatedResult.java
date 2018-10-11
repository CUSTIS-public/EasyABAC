package custis.easyabac.core.trace.model;

/**
 * Calculation result for policy
 */
public class CalculatedResult {
    private final String value;

    public CalculatedResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CalculatedResult of(int decision) {
        return new CalculatedResult(formatAbstractResultId(decision));
    }

    private static String formatAbstractResultId(int id) {
        switch (id) {
            case 0:
                return "DECISION_PERMIT";
            case 1:
                return "DECISION_DENY";
            case 2:
                return "DECISION_INDETERMINATE";
            case 3:
                return "DECISION_NOT_APPLICABLE";
            case 4:
                return "DECISION_INDETERMINATE_DENY";
            case 5:
                return "DECISION_INDETERMINATE_PERMIT";
            case 6:
                return "DECISION_INDETERMINATE_DENY_OR_PERMIT";
        }
        return "";
    }
}
