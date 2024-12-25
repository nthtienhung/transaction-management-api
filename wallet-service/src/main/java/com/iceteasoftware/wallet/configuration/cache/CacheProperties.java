package com.iceteasoftware.wallet.configuration.cache;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "cache")
@Getter
@Setter
public class CacheProperties {

    private Map<String, Integer> timeToLives;

    private String configType;
}



