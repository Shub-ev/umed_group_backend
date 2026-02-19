package com.ug.ug_inventory_management.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAdminNotFoundException(AdminNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        ), HttpStatus.BAD_REQUEST);             // Bad Request is 404 (used for invalid input, missing field)
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ErrorResponse> handleWrongPasswordException(WrongPasswordException exception) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        ), HttpStatus.UNAUTHORIZED);
    }
}
