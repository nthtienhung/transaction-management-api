package com.transactionservice.configuration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transactionservice.configuration.JacksonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public <T> void sendMessage(String topicName, T messageObject) {
        try {
            ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
            String message = objectMapper.writeValueAsString(messageObject);
            log.info("Sending message to topic {}: {}", topicName, message);

            // Thêm Producer Listener
            kafkaTemplate.setProducerListener(new ProducerListener<>() {
                @Override
                public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
                    log.info("Message sent successfully to topic: {}", producerRecord.topic());
                }

                @Override
                public void onError(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                    log.error("Error sending message to topic {}: {}", producerRecord.topic(), exception.getMessage());
                    handleFailedMessage(producerRecord);
                }
            });

            kafkaTemplate.send(topicName, message);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for topic {}: {}", topicName, messageObject, e);
        }
    }

    private void handleFailedMessage(ProducerRecord<String, String> record) {
        log.error("Message failed to send. Triggering rollback or compensation logic for topic: {}", record.topic());
        // Rollback hoặc gửi thông báo thất bại
    }
}
