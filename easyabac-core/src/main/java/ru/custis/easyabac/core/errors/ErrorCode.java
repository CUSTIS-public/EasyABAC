package ru.custis.easyabac.core.errors;

public enum ErrorCode {
    ALREADY_EXISTS("AlreadyExists"),
    ILLEGAL_VALUE("ILLEGAL_VALUE"),;

    private  final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
