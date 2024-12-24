package com.iceteasoftware.common.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Author: thinhtd
 * Date: 12/24/2024
 * Time: 9:34 AM
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException,
            ServletException {
        Throwable cause = authException.getCause();
        String message = "Unauthorized";

        if (cause instanceof ExpiredJwtException) {
            message = "Token expired";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":401,\"message\":\"" + message + "\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
    }

}
