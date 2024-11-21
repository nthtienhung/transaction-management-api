package com.example.iamservice.configuration.cache;

import com.example.iamservice.util.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "cache", name = "config-type", havingValue = "redisson",
        matchIfMissing = false)
public class CacheRedissonConfiguration {
    private String mode;

    private int cacheDuration;

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

    private String transportMode;

    private int database;

    private Single single;

    private Sentinel sentinel;

    @Getter
    @Setter
    public static class Single {
        private String address;

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

        private String readMode;

        private String subscriptionMode;

        private String[] nodes;

        private String masterName;

        private boolean checkSentinelsList;
    }

    public static enum Mode {
        SINGLE, SENTINEL
    }

    private final CacheProperties properties;

    @Bean("redissonClient")
    public RedissonClient redissonClient() {
        Config config = new Config();

        // common config

        config.setNettyThreads(this.nettyThreads);
        config.setThreads(this.threads);
        config.setTransportMode(TransportMode.valueOf(this.transportMode));

        // sentinel
        if (Validator.equals(this.mode, Mode.SENTINEL.name().toLowerCase())) {
            SentinelServersConfig sentinelConfig = config.useSentinelServers();

            // common config
            sentinelConfig.setClientName(this.clientName);

            if (Validator.isNotNull(this.password)) {
                sentinelConfig.setPassword(this.password);
            }

            sentinelConfig.setSubscriptionsPerConnection(this.subscriptionsPerConnection);
            sentinelConfig.setDatabase(this.database);
            sentinelConfig.setRetryAttempts(this.retryAttempts);
            sentinelConfig.setRetryInterval(this.retryInterval);
            sentinelConfig.setConnectTimeout(this.connectTimeout);
            sentinelConfig.setTimeout(this.timeout);
            sentinelConfig.setIdleConnectionTimeout(this.idleConnectionTimeout);

            // special config
            sentinelConfig.setMasterName(this.sentinel.getMasterName());
            sentinelConfig.addSentinelAddress(this.sentinel.getNodes());
            sentinelConfig.setFailedSlaveReconnectionInterval(this.sentinel.getFailedSlaveReconnectionInterval());
            sentinelConfig.setFailedSlaveCheckInterval(this.sentinel.getFailedSlaveCheckInterval());
            sentinelConfig
                    .setSubscriptionConnectionMinimumIdleSize(this.sentinel.getSubscriptionConnectionMinimumIdleSize());
            sentinelConfig.setSubscriptionConnectionPoolSize(this.sentinel.getSubscriptionConnectionPoolSize());
            sentinelConfig.setSlaveConnectionMinimumIdleSize(this.sentinel.getSlaveConnectionMinimumIdleSize());
            sentinelConfig.setSlaveConnectionPoolSize(this.sentinel.getSlaveConnectionPoolSize());
            sentinelConfig.setMasterConnectionMinimumIdleSize(this.sentinel.getMasterConnectionMinimumIdleSize());
            sentinelConfig.setMasterConnectionPoolSize(this.sentinel.getMasterConnectionPoolSize());
            sentinelConfig.setReadMode(ReadMode.valueOf(this.sentinel.getReadMode()));
            sentinelConfig.setSubscriptionMode(SubscriptionMode.valueOf(this.sentinel.getSubscriptionMode()));
            sentinelConfig.setCheckSentinelsList(this.sentinel.isCheckSentinelsList());

        } else { // single server
            SingleServerConfig singleConfig = config.useSingleServer();

            // common config
            singleConfig.setClientName(this.clientName);

            if (Validator.isNotNull(this.password)) {
                singleConfig.setPassword(this.password);
            }

            singleConfig.setSubscriptionsPerConnection(this.subscriptionsPerConnection);
            singleConfig.setDatabase(this.database);
            singleConfig.setRetryAttempts(this.retryAttempts);
            singleConfig.setRetryInterval(this.retryInterval);
            singleConfig.setConnectTimeout(this.connectTimeout);
            singleConfig.setTimeout(this.timeout);
            singleConfig.setIdleConnectionTimeout(this.idleConnectionTimeout);

            // special config
            // format as redis://127.0.0.1:7181 or rediss://127.0.0.1:7181 for SSL
            singleConfig.setAddress(this.single.getAddress());
            singleConfig.setSubscriptionConnectionMinimumIdleSize(this.single.getSubscriptionConnectionMinimumIdleSize());
            singleConfig.setSubscriptionConnectionPoolSize(this.single.getSubscriptionConnectionPoolSize());
            singleConfig.setConnectionMinimumIdleSize(this.single.getConnectionMinimumIdleSize());
            singleConfig.setConnectionPoolSize(this.single.getConnectionPoolSize());
            singleConfig.setDnsMonitoringInterval(this.single.getDnsMonitoringInterval());
        }

        return Redisson.create(config);
    }

    @Bean
    CacheManager cacheManager() {
        Map<String, CacheConfig> config = new HashMap<String, CacheConfig>();

        // define local cache settings for "default" cache.
        // ttl = {cacheDuration} minutes and maxIdleTime = {cacheDuration/2} minutes for local cache entries
        config.put("default", new CacheConfig(this.cacheDuration * 60 * 1000, this.cacheDuration * 60 * 1000 / 2));

        Map<String, Integer> timeTolives = this.properties.getTimeToLives();

        for (Map.Entry<String, Integer> entry : timeTolives.entrySet()) {
            String key = entry.getKey();
            Integer timeTolive = entry.getValue();

            config.put(key, new CacheConfig(timeTolive * 1000, timeTolive * 1000 / 2));
        }

        return new RedissonSpringCacheManager(redissonClient(), config);
    }

}
