package com.iceteasoftware.apigateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.iceteasoftware.apigateway.filter.JwtTokenInterceptor;

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

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000"); // Cho phép origin của frontend
        config.addAllowedMethod("*"); // Cho phép tất cả các phương thức (GET, POST, PUT, DELETE,...)
        config.addAllowedHeader("*"); // Cho phép tất cả các header
        config.setAllowCredentials(true); // Cho phép gửi cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
