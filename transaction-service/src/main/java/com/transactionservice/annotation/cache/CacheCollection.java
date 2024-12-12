package com.transactionservice.annotation.cache;

import java.lang.annotation.*;

/**
 * Annotation để đánh dấu các phương thức hoặc lớp cần được cache.
 *
 * <p>Annotation này cho phép cấu hình các thuộc tính liên quan đến cache như tên cache,
 * khóa, các thuộc tính để so sánh, và hành động cache khi thực hiện.</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheCollection {
    /**
     * Tên của cache mà sẽ được sử dụng.
     *
     * @return một mảng các tên cache
     */
    String[] cacheNames();
    /**
     * Khóa để xác định mục cache.
     *
     * @return khóa cache
     */
    String key();
    /**
     * Các thuộc tính để so sánh với mục cache hiện tại.
     *
     * @return một mảng các thuộc tính để so sánh
     */
    String[] compareProperties();
    /**
     * Điều kiện để áp dụng cache. Nếu điều kiện này không thỏa mãn,
     * cache sẽ không được áp dụng.
     *
     * @return điều kiện cache
     */
    String condition() default "";
    /**
     * Hành động cache cần thực hiện.
     * Mặc định là {@link CacheAction#PUT}.
     *
     * @return hành động cache
     */
    CacheAction action() default CacheAction.PUT;

}
