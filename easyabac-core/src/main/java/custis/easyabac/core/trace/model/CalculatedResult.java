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
                return "PERMIT";
            case 1:
                return "DENY";
            case 2:
                return "INDETERMINATE";
            case 3:
                return "NOT_APPLICABLE";
            case 4:
                return "INDETERMINATE";
            case 5:
                return "INDETERMINATE";
            case 6:
                return "INDETERMINATE";
        }
        return "";
    }

    @Override
    public String toString() {
        return value;
    }
}
