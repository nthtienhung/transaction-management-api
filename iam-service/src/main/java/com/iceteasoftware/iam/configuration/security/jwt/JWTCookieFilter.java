package com.iceteasoftware.iam.configuration.security.jwt;

import com.iceteasoftware.iam.constant.SecurityConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;


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
        } else {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(token != null && token.startsWith("Bearer")) {
                token = token.substring(7);
                try {
                    Authentication authentication = this.jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    log.error("Invalid token: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Invalid token\"}");
                    return;
                }
                filterChain.doFilter(request, response);
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
