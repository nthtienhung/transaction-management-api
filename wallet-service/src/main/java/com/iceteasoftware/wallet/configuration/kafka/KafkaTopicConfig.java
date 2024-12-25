package com.iceteasoftware.wallet.configuration.kafka;

import com.iceteasoftware.wallet.constant.KafkaTopicConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of("bootstrap.servers", bootstrapServers));
    }

    @Bean
    public List<NewTopic> kafkaTopics() {
        return List.of(
                createTopic(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SUCCESSFUL_UPDATE_WALLET),
                createTopic(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_WALLET),
//                createTopic(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_TRA),
                createTopic(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_TRANSACTION)
        );
    }

    private NewTopic createTopic(String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(KafkaTopicConstants.DEFAULT_KAFKA_PARTITIONS)
                .build();
    }
}