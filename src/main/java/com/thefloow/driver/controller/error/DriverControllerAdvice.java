package com.thefloow.driver.controller.error;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DriverControllerAdvice {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorMsg> handleServiceException(ServiceException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorMsg(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMsg> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMsg("bad.request", "Invalid input data"));
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorMsg> handleHttpMessageNotReadableException(ConversionFailedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMsg("bad.request", "Invalid input data"));
    }

}
