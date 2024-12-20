package com.transactionservice.configuration.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Author: thinhtd
 * Date: 12/19/2024
 * Time: 9:08 AM
 */
@Slf4j
@Component
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes == null) {
//            log.warn("No request attributes found. Feign call might not originate from an HTTP request.");
            return;
        }

        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
//        log.info("authHeader received: {}", authHeader);

        if (StringUtils.hasText(authHeader)) {
            // Kiểm tra định dạng "Bearer <token>"
            if (authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // Loại bỏ "Bearer "
//                log.info("Extracted token: {}", token);
                requestTemplate.header("Authorization", "Bearer " + token); // Gửi lại token (nếu cần)
            } else {
//                log.warn("Invalid Authorization header format: {}", authHeader);
            }
        } else {
//            log.warn("No Authorization header found in the incoming request.");
        }
    }
}
