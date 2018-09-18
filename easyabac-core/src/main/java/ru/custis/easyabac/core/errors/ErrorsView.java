package ru.custis.easyabac.core.errors;

import java.util.List;

public class ErrorsView {
    private final List<FieldError> fieldErrors;
    private final List<GlobalError> globalErrors;

    public ErrorsView(List<FieldError> fieldErrors, List<GlobalError> globalErrors) {
        this.fieldErrors = fieldErrors;
        this.globalErrors = globalErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public List<GlobalError> getGlobalErrors() {
        return globalErrors;
    }
}
