package com.iceteasoftware.iam.configuration.kafka;

import com.iceteasoftware.iam.constant.KafkaTopicConstants;
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
        return createTopic(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SIGN_UP);
    }

    @Bean
    public NewTopic topicSendEmailResetPassword() {
        return createTopic(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_FORGOT_PASSWORD);
    }

    private NewTopic createTopic(String topicName) {
        return TopicBuilder
                .name(topicName)
                .partitions(KafkaTopicConstants.DEFAULT_KAFKA_PARTITIONS)
                .build();
    }
}