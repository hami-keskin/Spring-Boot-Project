package com.example.OrderService.exception;

public class RecordNotFoundException extends CustomException {
    public RecordNotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}
