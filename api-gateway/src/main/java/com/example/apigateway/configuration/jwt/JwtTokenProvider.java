package com.example.apigateway.configuration.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey; // Inject từ cấu hình

    /**
     * Xác thực token JWT.
     * @param token JWT token.
     * @return true nếu token hợp lệ, false nếu không hợp lệ.
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token); // Gọi hàm getClaims để kiểm tra token
            return true;
        } catch (RuntimeException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Trích xuất role từ token JWT.
     * @param token JWT token.
     * @return Role của user, hoặc null nếu không tìm thấy.
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = getClaims(token); // Lấy claims từ token
            String role = claims.get("role", String.class); // Lấy role từ claims
            log.info("Extracted role: {}", role);
            return role;
        } catch (RuntimeException e) {
            log.error("Failed to extract role: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Lấy toàn bộ Claims từ token JWT.
     * @param token JWT token.
     * @return Claims chứa thông tin từ token.
     * @throws RuntimeException nếu token không hợp lệ.
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Failed to parse JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid token format: {}", e.getMessage());
            throw new RuntimeException("Token is empty or null", e);
        }
    }
}
