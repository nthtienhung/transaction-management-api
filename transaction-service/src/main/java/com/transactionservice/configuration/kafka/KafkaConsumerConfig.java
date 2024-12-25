package com.transactionservice.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094"); // Kafka broker
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-group"); // Group ID
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Key Deserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Value Deserializer
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start from earliest message
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Disable auto commit
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000); // Timeout session nếu không nhận được heartbeat
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10); // Số lượng record tối đa trong 1 lần poll
        return new KafkaConsumer<>(props);
    }
}