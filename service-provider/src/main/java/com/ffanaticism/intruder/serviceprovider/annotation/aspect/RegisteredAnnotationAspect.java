package com.ffanaticism.intruder.serviceprovider.annotation.aspect;

import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public record RegisteredAnnotationAspect(UserService userService) {
    @Around("@annotation(com.ffanaticism.intruder.serviceprovider.annotation.Registered)")
    public Object register(ProceedingJoinPoint joinPoint) throws Throwable {
        CompletableFuture.runAsync(() -> Arrays.stream(joinPoint.getArgs())
                .filter(entity -> entity instanceof User)
                .map(entity -> (User) entity)
                .forEach(userService::save));

        return joinPoint.proceed();
    }
}
