package com.transactionservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.transactionservice.client.UserClient;
import com.transactionservice.client.WalletClient;
import com.transactionservice.configuration.auditing.AuditorAwareConfig;
import com.transactionservice.configuration.kafka.KafkaProducer;
import com.transactionservice.constant.KafkaTopicConstants;
import com.transactionservice.dto.request.ConfirmTransactionRequest;
import com.transactionservice.dto.request.OTPRequest;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.request.email.EmailRequest;
import com.transactionservice.dto.request.email.EmailTransactionRequest;
import com.transactionservice.dto.response.UserResponse;
import com.transactionservice.dto.response.WalletResponse;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.MessageCode;
import com.transactionservice.enums.Status;
import com.transactionservice.exception.handler.BadRequestAlertException;
import com.transactionservice.exception.handler.NotFoundAlertException;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.service.TransactionService;
import com.transactionservice.util.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Author: thinhtd, minh quang
 * Date: 12/9/2024
 * Time: 3:27 PM
 */

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserClient userClient;
    private final WalletClient walletClient;
    private final KafkaProducer kafkaProducer;


    @Override
    public List<TransactionResponse> getRecentReceivedTransactionListByUser() {
        return List.of();
    }

    @Override
    public List<TransactionResponse> getRecentSentTransactionListByUser() {
        return List.of();
    }

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException {
        System.out.println("Transaction Request: " + transactionRequest);
        WalletResponse senderWallet = walletClient.getWalletByWalletCode(transactionRequest.getSenderWalletCode());
        WalletResponse receiverWallet = walletClient.getWalletByWalletCode(transactionRequest.getRecipientWalletCode());

        System.out.println("Sender Wallet: " + senderWallet);
        System.out.println("Receiver Wallet: " + receiverWallet);
        UserResponse senderUser = userClient.getUserById(senderWallet.getUserId());
        UserResponse receiverUser = userClient.getUserById(receiverWallet.getUserId());

        System.out.println(senderUser.toString());

        if (senderWallet.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new BadRequestAlertException(MessageCode.MSG4103);
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setTransactionCode(generateTransactionCode());
        transaction.setSenderWalletCode(senderWallet.getWalletCode());
        transaction.setRecipientWalletCode(receiverWallet.getWalletCode());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setStatus(Status.SUCCESS);
        transaction.setDescription(transactionRequest.getDescription());

        walletClient.updateWalletBalance(senderWallet.getWalletCode(), -transactionRequest.getAmount());
        walletClient.updateWalletBalance(receiverWallet.getWalletCode(), transactionRequest.getAmount());

        ThreadLocalUtil.setCurrentUser(senderWallet.getUserId());

        // Save Transaction to Database
        transactionRepository.save(transaction);

        TransactionResponse result = TransactionResponse.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transactionRequest.getSenderWalletCode())
                .senderMail(senderUser.getEmail())
                .recipientWalletCode(transactionRequest.getRecipientWalletCode())
                .recipientMail(receiverUser.getEmail())
                .amount(transactionRequest.getAmount())
                .status(String.valueOf(Status.SUCCESS))
                .description(transactionRequest.getDescription())
                .build();

        ThreadLocalUtil.remove();

        System.out.println("Transaction Result: " + result);

        EmailTransactionRequest emailTransactionRequest = new EmailTransactionRequest();
        emailTransactionRequest.setUserId(senderWallet.getUserId());
        emailTransactionRequest.setTopicName(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION);
        emailTransactionRequest.setData(new Gson().toJson(result));

        System.out.println("Email Transaction Request: " + emailTransactionRequest);

        // Send Notification via Kafka
        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION, emailTransactionRequest);

        // Build Response Object
        return result;
    }

    @Override
    public void generateOtp(EmailRequest request) throws JsonProcessingException {
        WalletResponse wallet = walletClient.getWalletByWalletCode(request.getWalletCode());
        UserResponse user = userClient.getUserById(wallet.getUserId());

        if (userClient.isEmailExists(user.getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG4100);
        }

        String otp = generateOtpString();
        redisTemplate.opsForValue().set(user.getEmail(), otp, 2, TimeUnit.MINUTES);

        OTPRequest data = OTPRequest.builder()
                .email(user.getEmail())
                .otp(otp)
                .amount(request.getAmount())
                .build();
        EmailTransactionRequest emailDTO = new EmailTransactionRequest();
        emailDTO.setUserId(wallet.getUserId());
        emailDTO.setData(new Gson().toJson(data));
        emailDTO.setTopicName(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_OTP);

        System.out.println("Email DTO: " + emailDTO);

        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_OTP, emailDTO);
    }

    @Override
    public TransactionResponse confirmTransactionWithOTP(ConfirmTransactionRequest confirmTransactionRequest) throws JsonProcessingException {
        WalletResponse senderWallet = walletClient.getWalletByWalletCode(confirmTransactionRequest.getSenderWalletCode());
        UserResponse senderUser = userClient.getUserById(senderWallet.getUserId());
        String storedOtp = redisTemplate.opsForValue().get(senderUser.getEmail());
        System.out.println("Stored OTP: " + storedOtp);
        // 1. Kiểm tra OTP
        if (confirmTransactionRequest.getOtp() == null) {
            throw new BadRequestAlertException(MessageCode.MSG4114);
        }
        if (storedOtp == null) {
            throw new BadRequestAlertException(MessageCode.MSG4113);
        }
        if (!storedOtp.equals(confirmTransactionRequest.getOtp())) {
            throw new BadRequestAlertException(MessageCode.MSG4112);
        }

        redisTemplate.delete(senderUser.getEmail());

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderWalletCode(confirmTransactionRequest.getSenderWalletCode());
        transactionRequest.setRecipientWalletCode(confirmTransactionRequest.getRecipientWalletCode());
        transactionRequest.setAmount(confirmTransactionRequest.getAmount());
        transactionRequest.setDescription(confirmTransactionRequest.getDescription());

        // 2. Hoàn tất giao dịch
        return createTransaction(transactionRequest);
    }


    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundAlertException(MessageCode.MSG4111));
    }

    private String generateTransactionCode() {
        // Generate unique transaction code
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateOtpString() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);

        return String.valueOf(otp);
    }
}
