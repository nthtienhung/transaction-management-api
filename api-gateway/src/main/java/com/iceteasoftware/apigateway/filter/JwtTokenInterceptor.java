package com.iceteasoftware.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = extractToken(request);
        if (token == null) {
            log.warn("Authorization header is missing or invalid");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization token missing");
            return false;
        }

        try {
            Claims claims = decodeToken(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
                log.warn("Access denied: role {} is not allowed", role);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: Admin role required.");
                return false;
            }

            request.setAttribute("X-User-Id", userId);
            request.setAttribute("X-Role", role);
            response.addHeader("X-User-Id", userId);
            response.addHeader("X-Role", role);
            log.info("Forwarding headers: X-User-Id={}, X-Role={}", userId, role);

        } catch (IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return false;
        }
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}