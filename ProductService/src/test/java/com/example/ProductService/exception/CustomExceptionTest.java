package com.example.ProductService.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomExceptionTest {

    @Test
    public void testCustomExceptionWithMessageAndErrorCode() {
        String message = "An error occurred";
        String errorCode = "ERROR_CODE_123";

        CustomException exception = assertThrows(CustomException.class, () -> {
            throw new CustomException(message, errorCode);
        });

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    }

    @Test
    public void testCustomExceptionWithRequiredArgsConstructor() {
        String errorCode = "ERROR_CODE_123";

        CustomException exception = new CustomException(errorCode);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    }

    @Test
    public void testCustomExceptionMessageIsNull() {
        String errorCode = "ERROR_CODE_123";

        CustomException exception = new CustomException(null, errorCode);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    }

    @Test
    public void testCustomExceptionErrorCodeIsNull() {
        String message = "An error occurred";

        CustomException exception = new CustomException(message, null);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isNull();
    }
}
