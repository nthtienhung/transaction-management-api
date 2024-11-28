package com.example.userservice.configuration.cache;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.BaseConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Configuration
@EnableCaching
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cache.redisson")
@ConditionalOnProperty(prefix = "cache", name = "config-type", havingValue = "redisson", matchIfMissing = false)
public class CacheRedissonConfiguration {

    private String mode; // Mode: single or sentinel
    private int cacheDuration; // Cache duration in minutes
    @Value("${cache.redisson.clientName:my-redis-client}")
    private String clientName;
    private String password;
    private int subscriptionsPerConnection;
    private int idleConnectionTimeout;
    private int connectTimeout;
    private int timeout;
    private int retryAttempts;
    private int retryInterval;
    private int threads;
    private int nettyThreads;
    private String transportMode; // NIO or EPOLL
    private int database;

    private Single single; // Single server configuration
    private Sentinel sentinel; // Sentinel configuration

    @Getter
    @Setter
    public static class Single {
        private String address; // Redis address (e.g., redis://localhost:6379)
        private int subscriptionConnectionMinimumIdleSize;
        private int subscriptionConnectionPoolSize;
        private int connectionMinimumIdleSize;
        private int connectionPoolSize;
        private long dnsMonitoringInterval;
    }

    @Getter
    @Setter
    public static class Sentinel {
        private int failedSlaveReconnectionInterval;
        private int failedSlaveCheckInterval;
        private int subscriptionConnectionMinimumIdleSize;
        private int subscriptionConnectionPoolSize;
        private int slaveConnectionMinimumIdleSize;
        private int slaveConnectionPoolSize;
        private int masterConnectionMinimumIdleSize;
        private int masterConnectionPoolSize;
        private String readMode; // SLAVE, MASTER, MASTER_SLAVE
        private String subscriptionMode; // SLAVE or MASTER
        private String[] nodes; // Sentinel nodes
        private String masterName; // Master name for sentinel
        private boolean checkSentinelsList;
    }

    public enum Mode {
        SINGLE, SENTINEL
    }

    @Bean("redissonClient")
    public RedissonClient redissonClient() {
        Config config = new Config();

        // Đảm bảo transportMode không bị null
        if (this.transportMode == null || this.transportMode.isEmpty()) {
            this.transportMode = "NIO"; // Giá trị mặc định
        }
        config.setTransportMode(TransportMode.valueOf(this.transportMode.toUpperCase()));

        // Kiểm tra giá trị timeout và retryInterval, thiết lập mặc định nếu bị thiếu
        int retryIntervalValue = this.retryInterval > 0 ? this.retryInterval : 1500;
        int idleTimeoutValue = this.idleConnectionTimeout > 0 ? this.idleConnectionTimeout : 10000;

        // Single mode
        if ("single".equalsIgnoreCase(this.mode)) {
            SingleServerConfig singleConfig = config.useSingleServer();
            singleConfig.setAddress(this.single.getAddress());
            singleConfig.setPassword(this.password);
            singleConfig.setClientName(this.clientName);
            singleConfig.setRetryAttempts(this.retryAttempts);
            singleConfig.setRetryInterval(retryIntervalValue);
            singleConfig.setIdleConnectionTimeout(idleTimeoutValue);
            singleConfig.setConnectTimeout(this.connectTimeout);
            singleConfig.setTimeout(this.timeout);
            singleConfig.setConnectionMinimumIdleSize(this.single.getConnectionMinimumIdleSize());
            singleConfig.setConnectionPoolSize(this.single.getConnectionPoolSize());
            singleConfig.setSubscriptionConnectionMinimumIdleSize(this.single.getSubscriptionConnectionMinimumIdleSize());
            singleConfig.setSubscriptionConnectionPoolSize(this.single.getSubscriptionConnectionPoolSize());
        } else {
            throw new IllegalArgumentException("Unsupported Redis mode: " + this.mode);
        }

        return Redisson.create(config);
    }

    private void configureCommonSettings(BaseConfig<?> config) {
        config.setClientName(this.clientName);

        if (this.password != null && !this.password.isEmpty()) {
            config.setPassword(this.password);
        }

        config.setSubscriptionsPerConnection(this.subscriptionsPerConnection);
        config.setRetryAttempts(this.retryAttempts);
        config.setRetryInterval(this.retryInterval);
        config.setConnectTimeout(this.connectTimeout);
        config.setTimeout(this.timeout);
        config.setIdleConnectionTimeout(this.idleConnectionTimeout);
    }

    @Bean
    CacheManager cacheManager() {
        Map<String, CacheConfig> cacheConfigs = new HashMap<>();
        cacheConfigs.put("default", new CacheConfig(this.cacheDuration * 60 * 1000, this.cacheDuration * 60 * 1000 / 2));

        return new RedissonSpringCacheManager(redissonClient(), cacheConfigs);
    }

    @PostConstruct
    public void validateConfiguration() {
        log.info("Initializing Redisson configuration: mode={}, clientName={}, transportMode={}", mode, clientName, transportMode);
        if (this.mode == null || (!this.mode.equalsIgnoreCase("single") && !this.mode.equalsIgnoreCase("sentinel"))) {
            throw new IllegalArgumentException("Invalid or missing Redis mode configuration: " + this.mode);
        }

        if ("single".equalsIgnoreCase(this.mode) && (this.single == null || this.single.getAddress() == null)) {
            throw new IllegalArgumentException("Redis single server address is not configured.");
        }
    }
}
