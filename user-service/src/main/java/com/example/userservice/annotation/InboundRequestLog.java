package com.example.userservice.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Mapping
@Documented
public @interface InboundRequestLog {
    boolean skipPwVerified() default false;
    boolean ekycRequired() default true;

}