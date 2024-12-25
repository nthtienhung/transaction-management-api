package com.transactionservice.configuration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transactionservice.dto.response.wallet.WalletTransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hibernate.TransactionException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final KafkaConsumer<String, String> kafkaConsumer;
    private final ObjectMapper objectMapper; // Dùng để parse JSON

    public WalletTransactionStatus listenForResponse(
            String transactionCode,
            List<String> topics,
            int timeoutSeconds
    ) {
        kafkaConsumer.subscribe(topics);
        ConsumerRecords<String, String> records;

        long timeout = timeoutSeconds * 1000L;
        long startTime = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - startTime < timeout) {
//        while (true) {
                records = kafkaConsumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        WalletTransactionStatus status = objectMapper.readValue(
                                record.value(),
                                WalletTransactionStatus.class
                        );

                        // Only process messages for this transaction
                        if (status.getTransactionCode().equals(transactionCode)) {
                            kafkaConsumer.commitSync();
                            return status;
                        }
                    } catch (Exception e) {
                        log.error("Error parsing message: {}", e.getMessage());
                    }
                }
            }
            throw new TransactionException("Timeout waiting for response");
        } finally {
            kafkaConsumer.unsubscribe();
        }
    }
}