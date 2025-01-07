package com.palagi.demo_park_api.exception;

public class CpfUniqueViolationException extends RuntimeException {

    public CpfUniqueViolationException(String s) {
        super(message);
    }
}
