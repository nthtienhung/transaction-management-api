package com.transactionservice.annotation.cache;

import com.transactionservice.annotation.cache.CacheAction;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheMap {

    String [] cacheNames();

    String key();

    String keyMap();

    String colection() default "";

    CacheAction action() default CacheAction.PUT;

    String condition();
}
