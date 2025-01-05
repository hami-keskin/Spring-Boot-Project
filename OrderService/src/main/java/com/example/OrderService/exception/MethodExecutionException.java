package com.example.OrderService.exception;

public class MethodExecutionException extends RuntimeException {
    public MethodExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
