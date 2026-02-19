package com.ug.ug_inventory_management.common.exceptions;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("Invalid password");
    }

    public WrongPasswordException(String message) {
        super(message);
    }
}
