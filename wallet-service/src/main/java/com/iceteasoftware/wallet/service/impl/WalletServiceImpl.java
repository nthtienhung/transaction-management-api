package com.iceteasoftware.wallet.service.impl;

import com.iceteasoftware.wallet.configuration.kafka.KafkaProducer;
import com.iceteasoftware.wallet.constant.KafkaTopicConstants;
import com.iceteasoftware.wallet.dto.request.wallet.UpdateWalletRequest;
import com.iceteasoftware.wallet.dto.request.wallet.WalletTransactionStatus;
import com.iceteasoftware.wallet.dto.response.WalletResponse;
import com.iceteasoftware.wallet.entity.Wallet;
import com.iceteasoftware.wallet.enums.MessageCode;
import com.iceteasoftware.wallet.enums.Stage;
import com.iceteasoftware.wallet.exception.handler.BadRequestAlertException;
import com.iceteasoftware.wallet.repository.WalletRepository;
import com.iceteasoftware.wallet.service.WalletService;
import com.iceteasoftware.wallet.util.ThreadLocalUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final RedisTemplate<String, String> redisTemplate;
    private static final String WALLET_CODE_PREFIX = "OLTP";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProducer kafkaProducer;

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

    @KafkaListener(topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_TRANSACTION, groupId = "wallet-group")
    public void processTransactionInitiated(String message) throws JsonProcessingException {
        UpdateWalletRequest request = objectMapper.readValue(message, UpdateWalletRequest.class);
        log.info("Received transaction initiation event: {}", request);
        try {

            if(!String.valueOf(Stage.CREATED).equals(redisTemplate.opsForValue().get(request.getTransactionCode()))){
                log.info("Transaction already processed: {}: {}", request.getTransactionCode() , redisTemplate.opsForValue().get(request.getTransactionCode()));
                return;
            }

            // 1. Validate số dư
            Wallet senderWallet = walletRepository.findByWalletCode(request.getSenderWalletCode());
            if (senderWallet.getBalance() < request.getAmount()) {
                throw new BadRequestAlertException(MessageCode.MSG4103);
            }

            // 2. Trừ tiền người gửi trong transaction
            try {
                deductMoneyFromSender(request, senderWallet);
            } catch (Exception ex) {
                handleDeductionFailure(request, "Failed to deduct amount: " + ex.getMessage());
            }
        } catch (Exception ex) {
            handleDeductionFailure(request, ex.getMessage());
        }
    }

    public void deductMoneyFromSender(UpdateWalletRequest request, Wallet senderWallet) throws JsonProcessingException {
        senderWallet.setBalance(senderWallet.getBalance() - request.getAmount());
        walletRepository.save(senderWallet);

        WalletTransactionStatus transactionStatus = new WalletTransactionStatus(
                request.getTransactionCode(),
                Stage.DEDUCTED,
                request.getSenderWalletCode(),
                request.getRecipientWalletCode(),
                request.getAmount(),
                null
        );

        redisTemplate.opsForValue().set(request.getTransactionCode(), String.valueOf(Stage.DEDUCTED));

        log.info("Deducted money from sender: {}", transactionStatus);
        kafkaTemplate.send(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SUCCESSFUL_DEDUCT_WALLET, objectMapper.writeValueAsString(transactionStatus)
        );
    }

    @KafkaListener(topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREDIT_WALLET, groupId = "wallet-group")
    public void processDeductedTransaction(String message) throws JsonProcessingException {
        UpdateWalletRequest request = objectMapper.readValue(message, UpdateWalletRequest.class);

        if (!String.valueOf(Stage.DEDUCTED).equals(redisTemplate.opsForValue().get(request.getTransactionCode()))) {
            log.info("Transaction already processed: {}", request.getTransactionCode());
            return;
        }

        try {
            addMoneyToRecipient(request);
        } catch (Exception ex) {
            kafkaProducer.sendMessage("TRANSACTION_FAILED",
                    new WalletTransactionStatus(
                            request.getTransactionCode(),
                            Stage.COMPENSATED,
                            request.getSenderWalletCode(),
                            request.getRecipientWalletCode(),
                            request.getAmount(),
                            ex.getMessage()
                    )
            );
        }
    }

    public void addMoneyToRecipient(UpdateWalletRequest request) throws JsonProcessingException {
        Wallet recipientWallet = walletRepository.findByWalletCode(request.getRecipientWalletCode());
        recipientWallet.setBalance(recipientWallet.getBalance() + request.getAmount());
        walletRepository.save(recipientWallet);

        log.info("Added money to recipient: {}", request.getRecipientWalletCode());

        kafkaTemplate.send(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SUCCESSFUL_CREDIT_WALLET,
                objectMapper.writeValueAsString(new WalletTransactionStatus(
                        request.getTransactionCode(),
                        Stage.CREDITED,
                        request.getSenderWalletCode(),
                        request.getRecipientWalletCode(),
                        request.getAmount(),
                        null
                ))
        );
    }

    private void handleDeductionFailure(UpdateWalletRequest request, String errorMessage) throws JsonProcessingException {
        kafkaProducer.sendMessage("TRANSACTION_FAILED",
                new WalletTransactionStatus(
                        request.getTransactionCode(),
                        Stage.COMPENSATED,
                        request.getSenderWalletCode(),
                        request.getRecipientWalletCode(),
                        request.getAmount(),
                        errorMessage
                )
        );
    }

    @KafkaListener(topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_COMPENSATION, groupId = "wallet-group")
    public void processCompensation(String message) throws JsonProcessingException {
        UpdateWalletRequest request = objectMapper.readValue(message, UpdateWalletRequest.class);

        if (!String.valueOf(Stage.COMPENSATED).equals(redisTemplate.opsForValue().get(request.getTransactionCode()))) {
            log.info("Transaction has not been processed yet: {}", request.getTransactionCode());
            return;
        }

        redisTemplate.delete(request.getTransactionCode());

        if (Stage.DEDUCTED == request.getStage()) {
            // Hoàn tiền cho người gửi
            Wallet senderWallet = walletRepository.findByWalletCode(request.getSenderWalletCode());
            senderWallet.setBalance(senderWallet.getBalance() + request.getAmount());
            walletRepository.save(senderWallet);

            log.info("Refunded money to sender: {}", request.getSenderWalletCode());
        } else if (Stage.CREDITED == request.getStage()) {
            // Hoàn tiền cả hai bên
            Wallet senderWallet = walletRepository.findByWalletCode(request.getSenderWalletCode());
            senderWallet.setBalance(senderWallet.getBalance() + request.getAmount());
            walletRepository.save(senderWallet);

            Wallet recipientWallet = walletRepository.findByWalletCode(request.getRecipientWalletCode());
            recipientWallet.setBalance(recipientWallet.getBalance() - request.getAmount());
            walletRepository.save(recipientWallet);
            log.info("Refunded money to both sender and recipient: {} - {}", request.getSenderWalletCode(), request.getRecipientWalletCode());
        }
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


