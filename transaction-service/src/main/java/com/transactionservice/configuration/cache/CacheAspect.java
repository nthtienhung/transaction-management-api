package com.transactionservice.configuration.cache;
import com.transactionservice.annotation.cache.CacheAction;
import com.transactionservice.annotation.cache.CacheCollection;
import com.transactionservice.annotation.cache.CacheMap;
import com.transactionservice.annotation.cache.CacheUpdate;
import com.transactionservice.util.ReflectionUtil;
import com.transactionservice.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
/**
 * Aspect để xử lý caching cho các phương thức được đánh dấu bởi {@link CacheCollection}.
 *
 * <p>Lớp này sử dụng AOP để tự động cập nhật cache sau khi một phương thức
 * đã được thực thi thành công. Nó kiểm tra điều kiện, xử lý các khóa cache,
 * và thực hiện các hành động cache tương ứng.</p>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final CacheManager cacheManager;
    /**
     * Phương thức được gọi sau khi một phương thức đã hoàn thành thành công.
     *
     * <p>Phương thức này sẽ cập nhật cache dựa trên thông tin từ annotation {@link CacheCollection}.
     * Nó kiểm tra điều kiện, lấy khóa và cập nhật cache với các giá trị mới.</p>
     *
     * @param joinPoint thông tin về phương thức đang thực thi
     * @param cacheCollection annotation {@link CacheCollection} được áp dụng
     * @param returnValue giá trị trả về từ phương thức
     * @throws Exception nếu có lỗi xảy ra trong quá trình xử lý
     */
    @AfterReturning(pointcut = "@annotation(cacheCollection)", returning = "returnValue")
    public void cacheCollectionUpdate(final JoinPoint joinPoint, CacheCollection cacheCollection,
                                      Object returnValue) throws Exception {
        log.debug("cacheCollectionUpdate has been called");

        String[] cacheNames = cacheCollection.cacheNames();
        String keyExpression = cacheCollection.key();
        String[] compareProperties = cacheCollection.compareProperties();
        String condition = cacheCollection.condition();
        CacheAction action = cacheCollection.action();

        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = this.getEvaluationContext(returnValue, joinPoint);

        // get and check condition value
        if (Validator.isNotNull(condition)) {

            Expression conditionExp = parser.parseExpression(condition);

            boolean conditionResult = conditionExp.getValue(context, Boolean.class);

            if (!conditionResult) {
                return;
            }
        }

        // get key value
        Expression keyExp = parser.parseExpression(keyExpression);

        Object keyValue = keyExp.getValue(context);

        Class<?> _class = returnValue.getClass();

        for (String cacheName : cacheNames) {
            // get cache by cache name
            Cache cache = this.cacheManager.getCache(cacheName);

            // if cache is null, do nothing
            if (cache == null) {
                continue;
            }

            ValueWrapper valueWrapper = cache.get(keyValue);

            // if nothing in cache, do nothing
            if (valueWrapper == null) {
                return;
            }

            Object ob = valueWrapper.get();

            if (Collection.class.isAssignableFrom(ob.getClass().getSuperclass())) {
                Iterator<?> iterator = ((Collection<?>) ob).iterator();

                // Create new instance of object
                Collection<Object> newCollection = (Collection<Object>) ob.getClass().newInstance();

                boolean found = false;

                while (iterator.hasNext()) {
                    Object c = iterator.next();

                    if (!_class.isAssignableFrom(c.getClass())) {
                        break;
                    }

                    if (!this.compare(c, returnValue, compareProperties)) {
                        newCollection.add(c);
                    } else {
                        found = true;

                        // if action = PUT, replace the found item with the new one
                        // if action = EVICT, ignore the found item
                        if (action == CacheAction.PUT) {
                            newCollection.add(returnValue);
                        }
                    }
                }

                // if action = PUT and the match item could not be found, add the new one to the collection
                if (!found && action == CacheAction.PUT) {
                    newCollection.add(returnValue);
                }

                // put the collection to the cache
                cache.put(keyValue, newCollection);
            }
        }
    }
    /**
     * Phương thức được gọi sau khi một phương thức đã hoàn thành thành công.
     *
     * <p>Phương thức này cập nhật cache dựa trên thông tin từ annotation {@link CacheMap}.
     * Nó kiểm tra điều kiện, lấy các khóa cache và cập nhật bản đồ cache với giá trị mới.</p>
     *
     * @param joinPoint thông tin về phương thức đang thực thi
     * @param cacheMap annotation {@link CacheMap} được áp dụng
     * @param returnValue giá trị trả về từ phương thức
     * @throws Exception nếu có lỗi xảy ra trong quá trình xử lý
     */
    @AfterReturning(pointcut = "@annotation(cacheMap)", returning = "returnValue")
    public void cacheMapUpdate(final JoinPoint joinPoint, CacheMap cacheMap,
                               Object returnValue) throws Exception {
        log.debug("cacheMapUpdate has been called");

        String[] cacheNames = cacheMap.cacheNames();
        String keyCacheExpression = cacheMap.key();
        String keyMapExpression = cacheMap.keyMap();
        String condition = cacheMap.condition();
        CacheAction action = cacheMap.action();

        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = this.getEvaluationContext(returnValue, joinPoint);

        if (Validator.isNotNull(condition)) {
            Expression conditionExp = parser.parseExpression(condition);

            boolean conditionResult = conditionExp.getValue(context, Boolean.class);

            if (!conditionResult) {
                return;
            }
        }

        // get key value
        Expression keyCacheExp = parser.parseExpression(keyCacheExpression);

        Object keyCacheValue = keyCacheExp.getValue(context);

        // get key map
        Expression keyMapExp = parser.parseExpression(keyMapExpression);

        Object keyMapValue = keyMapExp.getValue(context);

        for (String cacheName : cacheNames) {
            // get cache by cache name
            Cache cache = this.cacheManager.getCache(cacheName);

            // if cache is null, do nothing
            if (cache == null) {
                continue;
            }

            ValueWrapper valueWrapper = cache.get(keyCacheValue);

            // if nothing in cache, do nothing
            if (valueWrapper == null) {
                return;
            }

            Object ob = valueWrapper.get();

            if (Map.class.isAssignableFrom(ob.getClass().getSuperclass())) {

                Map<Object, Object> mapOb = (Map<Object, Object>) ob;

                if (action == CacheAction.PUT) {
                    // if the match item has be found, replace it with the new one
                    mapOb.put(keyMapValue, returnValue);
                } else if (action == CacheAction.EVICT) {
                    // if the match item has be found, remove it
                    mapOb.remove(keyMapValue);
                }

                // replace the map to the cache
                cache.put(keyCacheValue, mapOb);
            }
        }
    }

    @AfterReturning(pointcut = "@annotation(cacheUpdate)", returning = "returnValue")
    public void cacheUpdate(final JoinPoint joinPoint, CacheUpdate cacheUpdate,
                            Object returnValue) throws Exception {
        log.debug("cacheUpdate has been called");

        CacheCollection[] collections = cacheUpdate.collections();
        CacheMap[] maps = cacheUpdate.maps();

        for (CacheCollection collection : collections) {
            this.cacheCollectionUpdate(joinPoint, collection, returnValue);
        }

        for (CacheMap map : maps) {
            this.cacheMapUpdate(joinPoint, map, returnValue);
        }
    }

    /**
     * @param returnValue
     * @param compareProperties
     * @return
     */
    private boolean compare(Object origin, Object returnValue, String[] compareProperties) {
        for (String property : compareProperties) {
            Object originPv = ReflectionUtil.getFieldValue(origin, property);

            Object returnPv = ReflectionUtil.getFieldValue(returnValue, property);

            if (!Validator.equals(originPv, returnPv)) {
                return false;
            }
        }

        return true;
    }

    private StandardEvaluationContext getEvaluationContext(Object returnValue, final JoinPoint joinPoint) throws Exception {
        StandardEvaluationContext context = new StandardEvaluationContext(returnValue);

        // set return value to variables context
        context.setVariable("returnValue", returnValue);

        MethodSignature sign = (MethodSignature) joinPoint.getSignature();

        String[] parameterNames = sign.getParameterNames();

        Object[] args = joinPoint.getArgs();

        // get join point target class name
        String target = sign.getDeclaringTypeName();

        Class<?> clazz = Class.forName(target);

        context.setVariable("target", clazz);

        // add parameters to variables context
        if (Validator.isNotNull(parameterNames)) {
            for (int i = 0; i < parameterNames.length; i++) {
                String parameterName = parameterNames[i];

                if (i < args.length) {
                    context.setVariable(parameterName, args[i]);
                }
            }
        }

        return context;
    }
}

