package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.constant.GatewayCacheConstants;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.entity.UserLoginFailed;
import com.iceteasoftware.iam.repository.UserLoginFailedRepository;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.LogoutService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtSecret;
    private final UserLoginFailedRepository loginFailedRepository;
    private final UserRepository userRepository;
    @Caching(evict = {
            @CacheEvict(cacheNames = {GatewayCacheConstants.UserLoginFailed.FIND_BY_USER_ID}, key = "'userId_' + #userId")
    })
    @Override
    public void logout(HttpServletRequest request) {
        // Xóa thông tin trong SecurityContextHolder
        SecurityContextHolder.clearContext();

        // Lấy JWT từ header
        String jwt = getJwtFromHeader(request);
        if (jwt == null) {
            throw new IllegalArgumentException("JWT token is missing");
        }

        // Trích xuất email từ JWT
        String email = extractEmailFromJwt(jwt);
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email could not be extracted from JWT");
        }

        // Tìm user theo email
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        User user = optionalUser.get();

        // Truyền userId vào key của CacheEvict
        String userId = user.getUserId();

        // Xóa thông tin đăng nhập thất bại
        UserLoginFailed userLoginFailed = loginFailedRepository.findByUserId(userId);
        if (userLoginFailed != null) {
            loginFailedRepository.delete(userLoginFailed);
        }

        // Hủy session hiện tại
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Có thể thêm logic để blacklist JWT hoặc vô hiệu hóa token
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
