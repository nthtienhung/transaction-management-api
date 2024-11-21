package com.example.iamservice.service;


import com.example.iamservice.configuration.security.jwt.JWTAccessToken;
import com.example.iamservice.configuration.security.jwt.JWTToken;
import com.example.iamservice.constant.SecurityConstants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

public interface TokenService {
    @Cacheable(cacheNames = SecurityConstants.Cache.REFRESH_TOKEN, key = "#username", unless = "#result == null")
    default JWTToken getRefreshToken(String username) {
        return null;
    }
    @Cacheable(cacheNames = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username", unless = "#result == null")
    default JWTAccessToken getRememberMeToken(String username) {
        return null;
    }
    @Cacheable(cacheNames = SecurityConstants.Cache.TOKEN, key = "#username", unless = "#result == null")
    default JWTAccessToken getToken(String username) {
        return null;
    }
    @Caching(evict = {@CacheEvict(value = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username"),
            @CacheEvict(value = SecurityConstants.Cache.TOKEN, key = "#username"),
            @CacheEvict(value = SecurityConstants.Cache.REFRESH_TOKEN, key = "#username")})
    default String invalidateToken(String username) {
        return username;
    }
    @CachePut(cacheNames = SecurityConstants.Cache.REFRESH_TOKEN, key = "#username", unless = "#result == null")
    default JWTToken saveRefreshToken(String username, JWTToken token) {
        return token;
    }
    @Caching(
            put = {@CachePut(cacheNames = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username",
                    unless = "#result == null")},
            evict = {@CacheEvict(value = SecurityConstants.Cache.TOKEN, key = "#username", allEntries = true)})
    default JWTAccessToken saveRememberMeToken(String username, JWTAccessToken token) {
        return token;
    }
    @Caching(
            put = {@CachePut(cacheNames = SecurityConstants.Cache.TOKEN, key = "#username",
                    unless = "#result == null")},
            evict = {@CacheEvict(value = SecurityConstants.Cache.REMEMBER_ME_TOKEN, key = "#username",
                    allEntries = true)})
    default JWTAccessToken saveToken(String username, JWTAccessToken token) {
        return token;
    }



}
