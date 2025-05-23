package com.transactionservice.configuration.auditing;

import com.transactionservice.configuration.auditing.AuditorAwareConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistentConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareConfig();
    }
}
