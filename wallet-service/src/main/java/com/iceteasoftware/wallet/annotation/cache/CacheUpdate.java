package com.iceteasoftware.wallet.annotation.cache;

import com.iceteasoftware.wallet.annotation.cache.CacheCollection;
import com.iceteasoftware.wallet.annotation.cache.CacheMap;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheUpdate {
    CacheCollection[] collections() default {};

    CacheMap[] maps() default {};
}
