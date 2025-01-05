package com.example.OrderService.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MethodExecutionExceptionTest {

    @Test
    public void testMethodExecutionExceptionMessage() {
        String errorMessage = "An error occurred during method execution";
        Throwable cause = new RuntimeException("Root cause");

        MethodExecutionException exception = assertThrows(MethodExecutionException.class, () -> {
            throw new MethodExecutionException(errorMessage, cause);
        });

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testMethodExecutionExceptionWithoutCause() {
        String errorMessage = "An error occurred during method execution";

        MethodExecutionException exception = assertThrows(MethodExecutionException.class, () -> {
            throw new MethodExecutionException(errorMessage, null);
        });

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(null, exception.getCause());
    }

    @Test
    public void testMethodExecutionExceptionWithOnlyMessage() {
        String errorMessage = "An error occurred during method execution";

        MethodExecutionException exception = new MethodExecutionException(errorMessage, null);

        assertEquals(errorMessage, exception.getMessage());
    }
}
