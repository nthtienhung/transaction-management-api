package com.iceteasoftware.user.annotation.cache;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheUpdate {
    CacheCollection[] collections() default {};

    CacheMap[] maps() default {};
}
