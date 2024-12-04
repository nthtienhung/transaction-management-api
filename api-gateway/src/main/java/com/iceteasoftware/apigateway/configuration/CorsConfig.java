package com.iceteasoftware.apigateway.configuration;

import com.iceteasoftware.apigateway.filter.JwtTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
* Author: Tran Duc Thinh, Nguyen Minh Quang
* Date: 12/2/2024
* Time: 2:16 PM
*/

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor).addPathPatterns("/api/v1/config/**");
        System.out.println("JwtTokenInterceptor đã được đăng ký!");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Chỉ định origin
                .allowedMethods("GET", "POST", "PATCH", "DELETE")
                .allowCredentials(true);
    }

}
