package com.example.userservice.annotation.cache;

import java.lang.annotation.*;


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
