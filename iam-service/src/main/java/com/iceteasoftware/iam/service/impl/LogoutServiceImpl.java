package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.configuration.message.LabelKey;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.entity.UserLoginFailed;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.repository.UserLoginFailedRepository;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.LogoutService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtSecret;
    private final UserLoginFailedRepository loginFailedRepository;
    private final UserRepository userRepository;
    @Override
    public void logout(HttpServletRequest request) {
        // Xóa thông tin trong SecurityContextHolder
        SecurityContextHolder.clearContext();
        String jwt = getJwtFromHeader(request);
        String email = extractEmailFromJwt(jwt);
        Optional<User> user = userRepository.findByEmail(email);
        UserLoginFailed userLoginFailed = this.loginFailedRepository.findByUserId(user.get().getUserId());
        loginFailedRepository.delete(userLoginFailed);
        // Hủy session hiện tại
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    private String getJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header;
    }
    private String extractEmailFromJwt(String jwt) {
        try {
            // Trim and validate the JWT
            if (jwt == null || jwt.trim().isEmpty()) {
                throw new IllegalArgumentException("JWT string is null or empty");
            }
            jwt = jwt.trim();

            // Parse claims
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(jwt)
                    .getBody();

            return claims.getSubject();
        } catch (IllegalArgumentException e) {
            System.err.println("JWT string is null or empty: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported JWT: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing JWT: " + e.getMessage());
        }
        return null;
    }
}
