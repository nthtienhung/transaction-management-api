package com.example.iamservice.annotation.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheCollection {

    String[] cacheNames();

    String key();

    String[] compareProperties();

    String condition() default "";

    CacheAction action() default CacheAction.PUT;

}
