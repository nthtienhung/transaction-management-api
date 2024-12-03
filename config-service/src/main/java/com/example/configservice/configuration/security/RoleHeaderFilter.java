package com.example.configservice.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class RoleHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String roleHeader = request.getHeader("X-Role");

        if (roleHeader != null && !roleHeader.trim().isEmpty()) {
            try {
                String sanitizedRole = roleHeader.trim().startsWith("ROLE_")
                        ? roleHeader.trim()
                        : "ROLE_" + roleHeader.trim();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(sanitizedRole);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken("Anonymous", null, Collections.singletonList(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("Authentication set with role: " + sanitizedRole);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid role in X-Role header: " + roleHeader + e);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext(); // Dọn dẹp SecurityContext
        }
    }
}
