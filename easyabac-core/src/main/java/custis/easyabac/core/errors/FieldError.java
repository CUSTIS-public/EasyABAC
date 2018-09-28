package custis.easyabac.core.errors;

public class FieldError {
    private final String field;
    private final String code;
    private final Object rejectedValue;

    public FieldError(String field, String code, Object rejectedValue) {
        this.field = field;
        this.code = code;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}
