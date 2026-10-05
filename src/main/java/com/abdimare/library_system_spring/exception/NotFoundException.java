package com.abdimare.library_system_spring.exception;

// thrown when something the request refers to doesn't exist, turned into a 404 by the handler
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}