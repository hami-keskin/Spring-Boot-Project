package com.example.ProductService.aspect;

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

    // Tüm servis metodlarını hedef alan bir Pointcut tanımlayın
    @Pointcut("execution(* com.example.ProductService.service.*.*(..))")
    private void serviceMethods() {
    }

    // Tüm controller metodlarını hedef alan bir Pointcut tanımlayın
    @Pointcut("execution(* com.example.ProductService.controller.*.*(..))")
    private void controllerMethods() {
    }

    // Hem servis hem de controller metodlarını hedef alan bir Pointcut
    @Pointcut("serviceMethods() || controllerMethods()")
    private void allMethods() {
    }

    // Around advice: Hedef metodun öncesinde ve sonrasında çalışır
    @Around("allMethods()")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Yöntem çağrısı öncesi loglama
        logger.info("Executing method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            // Hata durumunda loglama
            logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), throwable.getCause() != null ? throwable.getCause() : "NULL");
            throw throwable;
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        // Yöntem çağrısı sonrası loglama
        logger.info("Method executed: {} returned: {} in: {}ms", joinPoint.getSignature(), result, timeTaken);

        return result;
    }
}