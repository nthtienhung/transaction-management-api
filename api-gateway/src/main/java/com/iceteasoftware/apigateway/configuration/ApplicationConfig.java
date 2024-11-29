package com.iceteasoftware.apigateway.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000/**") // chỉ định nguồn cụ thể
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS") // thêm OPTIONS nếu cần
                .allowCredentials(true); // nếu bạn cần gửi cookie hoặc thông tin xác thực
    }
}