package com.transactionservice.annotation.cache;

import com.transactionservice.annotation.cache.CacheCollection;
import com.transactionservice.annotation.cache.CacheMap;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheUpdate {
    CacheCollection[] collections() default {};

    CacheMap[] maps() default {};
}
