package com.thefloow.driver.controller.error;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    public ServiceException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "httpStatus=" + httpStatus +
                ", code='" + code + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
