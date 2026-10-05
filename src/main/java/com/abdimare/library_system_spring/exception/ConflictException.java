package com.abdimare.library_system_spring.exception;

// thrown when a request breaks a business rule, like borrowing a book that's already out, turned into a 409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}