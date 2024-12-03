package com.example.configservice.configuration.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class HeaderLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HeaderLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Interceptor đang được gọi!");
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-Role");

        logger.info("Interceptor called!");
        logger.info("X-User-Id: {}", userId);
        logger.info("X-Role: {}", role);

        if (userId == null || role == null) {
            log.warn("Headers X-User-Id hoặc X-Role không được truyền đến!");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // Kiểm tra role (nếu cần)
        if (!"ROLE_ADMIN".equals(role)) {
            log.warn("Role không hợp lệ, trả về 403.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }

}