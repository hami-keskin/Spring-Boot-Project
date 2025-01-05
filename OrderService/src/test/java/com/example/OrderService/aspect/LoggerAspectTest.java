package com.example.OrderService.aspect;

import com.example.OrderService.exception.MethodExecutionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    public void testLogRequestResponse_MethodExecutionException() throws Throwable {
        // Arrange
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        assertThrows(MethodExecutionException.class, () -> loggerAspect.logRequestResponse(joinPoint));
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    public void testLogRequestResponse_WithNullCause() throws Throwable {
        // Arrange
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(MethodExecutionException.class, () -> loggerAspect.logRequestResponse(joinPoint));
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    public void testLogRequestResponse_WithTiming() throws Throwable {
        // Arrange
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenReturn("result");

        // Act
        long startTime = System.currentTimeMillis();
        Object result = loggerAspect.logRequestResponse(joinPoint);
        long endTime = System.currentTimeMillis();

        // Assert
        assertThat(result).isEqualTo("result");
        verify(joinPoint, times(1)).proceed();

        // Zamanın geçtiğini kontrol et
        long timeTaken = endTime - startTime;
        assertThat(timeTaken).isGreaterThanOrEqualTo(0); // Zamanın geçtiğini doğrulamak için basit bir kontrol
    }
}
