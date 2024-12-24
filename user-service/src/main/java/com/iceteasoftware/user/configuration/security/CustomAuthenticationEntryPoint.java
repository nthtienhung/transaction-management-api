package com.iceteasoftware.user.configuration.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Author: thinhtd
 * Date: 12/18/2024
 * Time: 4:06 PM
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = "Unauthorized";
        Throwable cause = authException.getCause();

        if (cause instanceof ExpiredJwtException) {
            message = "Token expired";
        }

        // Log thông tin lỗi
        String requestURI = request.getRequestURI();
        log.info("Unauthorized request to: {} - Reason: {}", requestURI, message);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"status\":401,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message,
                LocalDateTime.now().toString()
        ));
    }
}
