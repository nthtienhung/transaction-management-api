package com.transactionservice.configuration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transactionservice.configuration.JacksonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public <T> void sendMessage(String topicName, T messageObject) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
            String message = objectMapper.writeValueAsString(messageObject);
            log.info("Sending message to topic {}: {}", topicName, message);
            kafkaTemplate.send(topicName, message);
        } catch (JsonProcessingException e){
            log.error("Failed to serialize message for topic {}: {}", topicName, messageObject, e);
        }
    }

    public <T> void sendMessage(String topicName, Map<String, Object> messageObject) {
        if (messageObject == null || topicName == null || topicName.isBlank()) {
            log.error("Invalid input: topicName or messageObject is null/empty.");
            return;
        }

        try {
            ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
            String message = objectMapper.writeValueAsString(messageObject);

            log.info("Sending message to topic {}: {}", topicName, message);

            // Gửi message đến Kafka
            kafkaTemplate.send(topicName, message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for topic {}: {}", topicName, messageObject, e);
        } catch (Exception ex) {
            log.error("Unexpected error while sending message to topic {}: {}", topicName, messageObject, ex);
        }
    }

}
