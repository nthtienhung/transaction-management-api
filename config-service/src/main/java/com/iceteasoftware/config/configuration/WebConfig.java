package com.iceteasoftware.config.configuration;

import com.iceteasoftware.config.configuration.security.HeaderLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private final HeaderLoggingInterceptor headerLoggingInterceptor;

    public WebConfig(HeaderLoggingInterceptor headerLoggingInterceptor) {
        this.headerLoggingInterceptor = headerLoggingInterceptor;
        log.info("WebConfig initialized!");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Adding HeaderLoggingInterceptor...");
        registry.addInterceptor(headerLoggingInterceptor)
                .addPathPatterns("/**") // Đảm bảo interceptor được áp dụng cho mọi route
                .order(1); // Đảm bảo thứ tự thực thi
    }

}
