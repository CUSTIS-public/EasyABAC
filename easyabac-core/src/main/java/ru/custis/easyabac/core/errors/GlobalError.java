package ru.custis.easyabac.core.errors;

public class GlobalError {
    private final String code;

    public GlobalError(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

