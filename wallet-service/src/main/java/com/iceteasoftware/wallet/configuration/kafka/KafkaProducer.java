package com.iceteasoftware.wallet.configuration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceteasoftware.wallet.configuration.JacksonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

}
