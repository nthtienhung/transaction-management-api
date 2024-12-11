package com.iceteasoftware.wallet.service.impl;

import com.iceteasoftware.wallet.constant.KafkaTopicConstants;
import com.iceteasoftware.wallet.dto.response.WalletResponse;
import com.iceteasoftware.wallet.entity.Wallet;
import com.iceteasoftware.wallet.repository.WalletRepository;
import com.iceteasoftware.wallet.service.WalletService;
import com.iceteasoftware.wallet.util.ThreadLocalUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:48 AM
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WalletRepository walletRepository;
    private static final String WALLET_CODE_PREFIX = "OLTP";

    /**
     * Listens for wallet creation messages from a Kafka topic and creates a wallet for the specified user.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Parses the incoming Kafka message to extract user ID and currency information.</li>
     *     <li>Sets the user ID in the current thread context for tracking purposes.</li>
     *     <li>Saves a new wallet entry with the provided user ID and initial currency balance to the database.</li>
     *     <li>Removes the user ID from the thread context after processing is complete.</li>
     * </ul>
     *
     * @param createWalletMessage the JSON message received from the Kafka topic, containing wallet details.
     * @throws JsonProcessingException if the message cannot be parsed as JSON.
     */
    @Override
    @Transactional
    @KafkaListener(topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_WALLET, groupId = "create-group")
    public void createWallet(String createWalletMessage) throws JsonProcessingException {
        log.info("Received message: {}", createWalletMessage);
        JsonNode jsonNode = objectMapper.readTree(createWalletMessage);
        String userId = jsonNode.get("userId").asText();
        Long balance = jsonNode.get("balance").asLong();

        ThreadLocalUtil.setCurrentUser(userId);

        walletRepository.save(Wallet.builder()
                .balance(balance)
                .userId(userId)
                .walletCode(generateWalletCode())
                .build());

        ThreadLocalUtil.remove();
    }

    @Override
    public WalletResponse getWalletByCode(String walletCode) {
        Wallet wallet = walletRepository.findByWalletCode(walletCode);

        return WalletResponse.builder()
                .walletCode(wallet.getWalletCode())
                .balance(wallet.getBalance())
                .userId(wallet.getUserId())
                .build();
    }

    @Override
    public void updateWalletBalance(String walletCode, Long amount) {
        Wallet wallet = walletRepository.findByWalletCode(walletCode);
        wallet.setBalance(wallet.getBalance() + amount);
        System.out.println(wallet.toString());
        walletRepository.save(wallet);
    }

    @Override
    public String getUserIdByWalletCode(String walletCode) {
        return walletRepository.findUserIdByWalletCode(walletCode);
    }

    @Override
    public WalletResponse getWalletByUserId(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId);
        return WalletResponse.builder()
                .walletCode(wallet.getWalletCode())
                .balance(wallet.getBalance())
                .userId(wallet.getUserId())
                .build();

    }

    private String generateWalletCode() {
        Random random = new Random();
        StringBuilder randomDigits = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            randomDigits.append(random.nextInt(10));
        }

        return WALLET_CODE_PREFIX + randomDigits.toString();
    }
}


