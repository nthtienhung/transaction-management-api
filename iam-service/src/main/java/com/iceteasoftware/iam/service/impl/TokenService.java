package com.iceteasoftware.iam.service.impl;


import com.iceteasoftware.iam.configuration.security.jwt.JWTAccessToken;
import com.iceteasoftware.iam.configuration.security.jwt.JWTToken;
import com.iceteasoftware.iam.constant.SecurityConstants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Cacheable(cacheNames = SecurityConstants.Cache.REFRESH_TOKEN, key = "#username", unless = "#result == null")
    public JWTToken getRefreshToken(String username) {
        return null;
    }

    @Cacheable(cacheNames = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username", unless = "#result == null")
    public JWTAccessToken getRememberMeToken(String username) {
        return null;
    }

    @Cacheable(cacheNames = SecurityConstants.Cache.TOKEN, key = "#username", unless = "#result == null")
    public JWTAccessToken getToken(String username) {
        return null;
    }

    @Caching(evict = {
            @CacheEvict(value = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username"),
            @CacheEvict(value = SecurityConstants.Cache.TOKEN, key = "#username"),
            @CacheEvict(value = SecurityConstants.Cache.REFRESH_TOKEN, key = "#username")})
    public String invalidateToken(String username) {
        return username;
    }

    @CachePut(cacheNames = SecurityConstants.Cache.REFRESH_TOKEN, key = "#username", unless = "#result == null")
    public JWTToken saveRefreshToken(String username, JWTToken token) {
        return token;
    }

    @Caching(
            put = {@CachePut(cacheNames = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username", unless = "#result == null")},
            evict = {@CacheEvict(value = SecurityConstants.Cache.TOKEN, key = "#username", allEntries = true)})
    public JWTAccessToken saveRememberMeToken(String username, JWTAccessToken token) {
        return token;
    }

    @Caching(
            put = {@CachePut(cacheNames = SecurityConstants.Cache.TOKEN, key = "#username", unless = "#result == null")},
            evict = {@CacheEvict(value = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username", allEntries = true)})
    public JWTAccessToken saveToken(String username, JWTAccessToken token) {
        return token;
    }
}