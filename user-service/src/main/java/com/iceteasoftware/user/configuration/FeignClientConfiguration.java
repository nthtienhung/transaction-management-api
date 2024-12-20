package com.iceteasoftware.user.configuration;

import com.iceteasoftware.user.configuration.security.AuthenticationRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new AuthenticationRequestInterceptor();
    }
}
