package com.example.userservice.configuration.security.jwt;

import com.example.userservice.configuration.security.jwt.JWTTokenProvider;
import com.example.userservice.constant.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@Slf4j
@Component
public class JWTCookieFilter extends OncePerRequestFilter {
    private final JWTTokenProvider<?> jwtTokenProvider;

    public JWTCookieFilter(JWTTokenProvider<?> jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {

            Cookie accessCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(SecurityConstants.Cookie.ACCESS_TOKEN))
                    .findFirst()
                    .orElse(null);

            if (accessCookie != null) {
                String accessToken = accessCookie.getValue();

                log.info("accessCookie: {}", accessToken);

                String csrfToken = resolveCsrfToken(request);

                log.info("csrfToken: {}", csrfToken);

                if (StringUtils.hasText(accessToken) && StringUtils.hasText(csrfToken)
                        && this.jwtTokenProvider.validateToken(accessToken, csrfToken)) {
                    Authentication authentication =
                            this.jwtTokenProvider.getAuthentication(accessToken);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveCsrfToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstants.Header.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.Header.BEARER_START)) {
            return bearerToken.substring(SecurityConstants.Header.BEARER_START.length());
        }

        return null;
    }

}
