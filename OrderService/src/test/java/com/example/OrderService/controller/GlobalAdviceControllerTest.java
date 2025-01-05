package com.example.OrderService.controller;

import com.example.OrderService.exception.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalAdviceControllerTest {

    private GlobalAdviceController globalAdviceController;

    @BeforeEach
    public void setUp() {
        globalAdviceController = new GlobalAdviceController();
    }

    @Test
    public void testHandleRecordNotFoundException() {
        RecordNotFoundException ex = new RecordNotFoundException("Record not found");
        ResponseEntity<String> response = globalAdviceController.handleRecordNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Record not found");
    }

    @Test
    public void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument");
        ResponseEntity<String> response = globalAdviceController.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Illegal argument");
    }

    @Test
    public void testHandleValidationException() {
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Map<String, String>> response = globalAdviceController.handleValidationException(ex);

        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("field", "defaultMessage");
        expectedErrors.put("error", "Validation failed");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(expectedErrors);
    }

    @Test
    public void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON request", (Throwable) null);
        ResponseEntity<String> response = globalAdviceController.handleHttpMessageNotReadableException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Malformed JSON request");
    }

    @Test
    public void testHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("paramName", "String");
        ResponseEntity<String> response = globalAdviceController.handleMissingServletRequestParameterException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Missing required parameter: paramName");
    }

    @Test
    public void testHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("paramName");

        ResponseEntity<String> response = globalAdviceController.handleMethodArgumentTypeMismatchException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Argument type mismatch for parameter: paramName");
    }

    @Test
    public void testHandleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        ResponseEntity<String> response = globalAdviceController.handleHttpRequestMethodNotSupportedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isEqualTo("HTTP method not supported: POST");
    }

    @Test
    public void testHandleException() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<String> response = globalAdviceController.handleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("An unexpected error occurred");
    }
}
