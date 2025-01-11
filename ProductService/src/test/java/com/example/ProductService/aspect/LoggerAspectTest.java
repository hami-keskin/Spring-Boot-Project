package com.example.ProductService.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class LoggerAspectTest {

    private LoggerAspect loggerAspect;
    private ProceedingJoinPoint joinPoint;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        loggerAspect = new LoggerAspect();
        joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        logger = LoggerFactory.getLogger(LoggerAspect.class);
    }

    @Test
    public void testLogRequestResponse_Success() throws Throwable {
        // Arrange
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenReturn("result");

        // Act
        Object result = loggerAspect.logRequestResponse(joinPoint);

        // Assert
        assertThat(result).isEqualTo("result");
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    public void testLogRequestResponse_WithException() throws Throwable {
        // Arrange
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> loggerAspect.logRequestResponse(joinPoint));
        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    public void testLogRequestResponse_ExecutionTime() throws Throwable {
        // Arrange
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(100); // Simulate method execution time
            return "result";
        });

        // Act
        Object result = loggerAspect.logRequestResponse(joinPoint);

        // Assert
        assertThat(result).isEqualTo("result");
        verify(joinPoint, times(1)).proceed();
        // Execution time logging can be checked by asserting log messages if needed
    }
}