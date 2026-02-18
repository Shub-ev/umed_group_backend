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
}
