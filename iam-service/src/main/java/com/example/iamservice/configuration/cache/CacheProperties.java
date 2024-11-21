package com.example.iamservice.configuration.cache;
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

    public Map<String, Integer> getTimeToLives() {
        return timeToLives;
    }

    public CacheProperties setTimeToLives(Map<String, Integer> timeToLives) {
        this.timeToLives = timeToLives;
        return this;
    }

    public String getConfigType() {
        return configType;
    }

    public CacheProperties setConfigType(String configType) {
        this.configType = configType;
        return this;
    }
}


