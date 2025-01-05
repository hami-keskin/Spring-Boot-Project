package com.example.ProductService.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordNotFoundExceptionTest {

    @Test
    public void testRecordNotFoundExceptionWithMessage() {
        String message = "Record not found";

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            throw new RecordNotFoundException(message);
        });

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("NOT_FOUND");
    }

    @Test
    public void testRecordNotFoundExceptionWithNullMessage() {
        RecordNotFoundException exception = new RecordNotFoundException(null);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("NOT_FOUND");
    }
}
