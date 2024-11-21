package com.example.iamservice.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AuthenticationProperties {
    @Value("${security.authentication.jwt.base64-secret}")
    private String base64Secret;

    @Value("${security.authentication.cookie.domain-name}")
    private String domainName;

    @Value("${security.authentication.cookie.enable-ssl}")
    private boolean enableSsl;

    @Value("${security.authentication.cookie.path}")
    private String path;

    @Value("${security.authentication.cookie.http-only}")
    private boolean httpOnly;

    @Value("${security.authentication.cookie.same-site}")
    private String sameSite;

    @Value("${security.cache.url-patterns}")
    private String[] urlPatterns;

    @Value("${security.cache.max-age}")
    private long cacheMaxAge;

    @Value("${security.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${security.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${security.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${security.cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${security.cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${security.cors.max-age}")
    private long maxAge;

    @Value("${security.login.max-attempt-time}")
    private int loginMaxAttemptTime;

    @Value("${security.password.max-attempt-time}")
    private int passwordMaxAttemptTime;
}

