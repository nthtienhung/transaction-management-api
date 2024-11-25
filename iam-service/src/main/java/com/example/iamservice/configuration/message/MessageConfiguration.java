package com.example.iamservice.configuration.message;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Component
@ConfigurationProperties(prefix = "spring.message")
@Setter
public class MessageConfiguration {

    private String encoding;

    private String basename;

    private int cacheDuration;

    private boolean useCodeAsDefaultMessage;

    /**
     * Locale resolver.
     *
     * @return the locale resolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Labels.VN);
        return localeResolver;
    }

    /**
     * Bundle message source.
     *
     * @return the message source
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/labels"); // Tên basename (không cần phần mở rộng .properties)
        messageSource.setDefaultEncoding("UTF-8"); // Encoding
        messageSource.setUseCodeAsDefaultMessage(true); // Dùng code làm thông điệp mặc định nếu không có trong resource
        return messageSource;
    }

}
