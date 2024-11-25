package com.example.iamservice.configuration.kafka;

import com.example.iamservice.constant.KafkaTopicConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of("bootstrap.servers", bootstrapServers));
    }

    @Bean
    public NewTopic topicSendEmailSignUp() {
        return TopicBuilder
                .name(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SIGN_UP)
                .partitions(KafkaTopicConstants.DEFAULT_KAFKA_PARTITIONS)
                .build();
    }

    @Bean
    public NewTopic topicSendEmailResetPassword() {
        return TopicBuilder
                .name(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_FORGOT_PASSWORD)
                .partitions(KafkaTopicConstants.DEFAULT_KAFKA_PARTITIONS)
                .build();
    }
}
