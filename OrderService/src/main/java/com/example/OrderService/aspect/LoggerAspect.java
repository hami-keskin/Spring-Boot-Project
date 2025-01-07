package com.example.OrderService.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Pointcut("@annotation(com.example.OrderService.annotation.RequestLogger)")
    private void methodRequestLogger() {
    }

    @Pointcut("@within(com.example.OrderService.annotation.RequestLogger)")
    private void classRequestLogger() {
    }

    @Pointcut("methodRequestLogger() || classRequestLogger()")
    private void requestLoggerMethods() {
    }

    @Around("requestLoggerMethods()")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        logger.info("Executing method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("Exception occurred in method: {} with cause: {}", joinPoint.getSignature(), throwable.getCause() != null ? throwable.getCause() : "NULL");
            throw throwable; // Orijinal istisnayı yeniden fırlat
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("Method executed: {} returned: {} in: {}ms", joinPoint.getSignature(), result, timeTaken);

        return result;
    }
}