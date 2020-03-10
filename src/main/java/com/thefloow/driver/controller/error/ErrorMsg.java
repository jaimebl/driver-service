package com.thefloow.driver.controller.error;

public class ErrorMsg {

    private final String code;
    private final String message;

    public ErrorMsg(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

