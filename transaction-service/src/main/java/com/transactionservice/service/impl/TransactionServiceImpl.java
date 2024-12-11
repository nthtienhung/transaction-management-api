package com.transactionservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.transactionservice.client.UserClient;
import com.transactionservice.client.WalletClient;
import com.transactionservice.configuration.auditing.AuditorAwareConfig;
import com.transactionservice.configuration.kafka.KafkaProducer;
import com.transactionservice.constant.KafkaTopicConstants;
import com.transactionservice.dto.request.*;
import com.transactionservice.dto.request.email.EmailRequest;
import com.transactionservice.dto.request.email.EmailTransactionRequest;
import com.transactionservice.dto.response.*;
import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.MessageCode;
import com.transactionservice.enums.Status;
import com.transactionservice.exception.handler.BadRequestAlertException;
import com.transactionservice.exception.handler.NotFoundAlertException;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.repository.TransactionRepositoryCustom;
import com.transactionservice.service.TransactionService;
import com.transactionservice.util.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Author: thinhtd
 * Date: 12/9/2024
 * Time: 3:27 PM
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepositoryCustom transactionRepositoryCustom;
    private final TransactionRepository transactionRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserClient userClient;
    private final WalletClient walletClient;
    private final KafkaProducer kafkaProducer;
    private final Gson gson;


    @Override
    public List<TransactionResponse> getRecentReceivedTransactionListByUser() {
        return List.of();
    }

    @Override
    public List<TransactionResponse> getRecentSentTransactionListByUser() {
        return List.of();
    }

    @Override
    public Page<TransactionListResponse> getTransactionListByUser(TransactionListRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Transaction> transactionsPage = transactionRepositoryCustom.findFilteredTransactions(
                request.getWalletCodeByUserLogIn(),
                request.getWalletCodeByUserSearch(),
                request.getTransactionCode(),
                request.getStatus(),
                request.getFromDate(),
                request.getToDate(),
                pageable
        );
        log.info("Transactions Page: {}", transactionsPage);


        // Map `Transaction` entities to `TransactionListResponse`
        List<TransactionListResponse> responseList = transactionsPage.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        // Return mapped results as Page<TransactionListResponse>
        return new org.springframework.data.domain.PageImpl<>(
                responseList,
                pageable,
                transactionsPage.getTotalElements()
        );
    }

    private TransactionListResponse mapToDto(Transaction transaction) {
        String recipientWalletCode = transaction.getRecipientWalletCode();

        // Lấy userId từ recipientWalletCode
        String userId = walletClient.getUserIdByWalletCode(recipientWalletCode);

        // Lấy thông tin user (firstName, lastName) từ userId
        FullNameResponse fullNameResponse = userClient.getFullNameByUserId(userId);

        return TransactionListResponse.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transaction.getSenderWalletCode())
                .receiverWalletCode(transaction.getRecipientWalletCode())
                .amount(transaction.getAmount())
                .status(String.valueOf(transaction.getStatus()))
                .description(transaction.getDescription())
                .FirstName(fullNameResponse.getFirstName())
                .LastName(fullNameResponse.getLastName())
                .createdAt(transaction.getCreatedDate())
                .build();
    }

    /**
     * Creates a new money transfer transaction.
     *
     * @param transactionRequest Transaction details
     * @return Created transaction information
     * @throws JsonProcessingException JSON processing error
     */
    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException {
        log.info("Creating transaction: {}", transactionRequest);

        // Fetch sender and receiver wallet information
        WalletResponse senderWallet = walletClient.getWalletByWalletCode(transactionRequest.getSenderWalletCode());
        WalletResponse receiverWallet = walletClient.getWalletByWalletCode(transactionRequest.getRecipientWalletCode());

        // Fetch user information
        UserResponse senderUser = userClient.getUserById(senderWallet.getUserId());
        UserResponse receiverUser = userClient.getUserById(receiverWallet.getUserId());

        // Check wallet balance
        if (senderWallet.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new BadRequestAlertException(MessageCode.MSG4103);
        }

        // Create transaction object
        Transaction transaction = buildTransaction(transactionRequest, senderWallet, receiverWallet);

        // Update wallet balances
        updateWalletBalances(senderWallet, receiverWallet, transactionRequest.getAmount());

        // Save transaction
        ThreadLocalUtil.setCurrentUser(senderWallet.getUserId());
        transactionRepository.save(transaction);

        // Create response
        TransactionResponse result = buildTransactionResponse(transaction, senderUser, receiverUser);
        ThreadLocalUtil.remove();

        // Send notification via Kafka
        sendTransactionNotification(senderWallet, result);

        return result;
    }

    /**
     * Generates and sends an OTP for a transaction.
     *
     * @param request Email information to send OTP
     * @throws JsonProcessingException JSON processing error
     */
    @Override
    public void generateOtp(EmailRequest request) throws JsonProcessingException {
        WalletResponse wallet = walletClient.getWalletByWalletCode(request.getWalletCode());
        UserResponse user = userClient.getUserById(wallet.getUserId());

        if (userClient.isEmailExists(user.getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG4100);
        }

        String otp = generateOtpString();
        redisTemplate.opsForValue().set(user.getEmail(), otp, 2, TimeUnit.MINUTES);

        OTPRequest otpData = OTPRequest.builder()
                .email(user.getEmail())
                .otp(otp)
                .amount(request.getAmount())
                .build();

        EmailTransactionRequest emailDTO = buildEmailTransactionRequest(
                wallet.getUserId(),
                otpData,
                KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_OTP
        );

        log.info("Sending OTP email: {}", emailDTO);
        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_OTP, emailDTO);
    }

    /**
     * Confirms a transaction using OTP.
     *
     * @param confirmTransactionRequest Transaction confirmation request
     * @return Confirmed transaction information
     * @throws JsonProcessingException JSON processing error
     */
    @Override
    @Transactional
    public TransactionResponse confirmTransactionWithOTP(ConfirmTransactionRequest confirmTransactionRequest) throws JsonProcessingException {
        // Fetch wallet and user information
        WalletResponse senderWallet = walletClient.getWalletByWalletCode(confirmTransactionRequest.getSenderWalletCode());
        UserResponse senderUser = userClient.getUserById(senderWallet.getUserId());

        // Validate OTP
        validateOTP(senderUser.getEmail(), confirmTransactionRequest.getOtp());

        // Remove OTP from Redis after validation
        redisTemplate.delete(senderUser.getEmail());

        // Create transaction request from confirmation details
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .senderWalletCode(confirmTransactionRequest.getSenderWalletCode())
                .recipientWalletCode(confirmTransactionRequest.getRecipientWalletCode())
                .amount(confirmTransactionRequest.getAmount())
                .description(confirmTransactionRequest.getDescription())
                .build();

        // Complete transaction
        return createTransaction(transactionRequest);
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * @param transactionId Transaction identifier
     * @return Transaction information
     * @throws NotFoundAlertException If transaction is not found
     */
    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundAlertException(MessageCode.MSG4111));
    }

    /**
     * Generates a random transaction code.
     *
     * @return Unique transaction code
     */
    private String generateTransactionCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Generates a random OTP.
     *
     * @return 6-digit OTP
     */
    private String generateOtpString() {
        Random rand = new Random();
        return String.format("%06d", 100000 + rand.nextInt(900000));
    }

    /**
     * Builds a transaction object from the transaction request.
     *
     * @param transactionRequest Transaction request details
     * @param senderWallet Sender's wallet information
     * @param receiverWallet Receiver's wallet information
     * @return Constructed Transaction entity
     */
    private Transaction buildTransaction(TransactionRequest transactionRequest,
                                         WalletResponse senderWallet,
                                         WalletResponse receiverWallet) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setTransactionCode(generateTransactionCode());
        transaction.setSenderWalletCode(senderWallet.getWalletCode());
        transaction.setRecipientWalletCode(receiverWallet.getWalletCode());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setStatus(Status.SUCCESS);
        transaction.setDescription(transactionRequest.getDescription());
        return transaction;
    }

    /**
     * Updates the balances of sender and receiver wallets.
     *
     * @param senderWallet Wallet of the sender
     * @param receiverWallet Wallet of the receiver
     * @param amount Transaction amount
     */
    private void updateWalletBalances(WalletResponse senderWallet,
                                      WalletResponse receiverWallet,
                                      Long amount) {
        walletClient.updateWalletBalance(senderWallet.getWalletCode(), -amount);
        walletClient.updateWalletBalance(receiverWallet.getWalletCode(), amount);
    }

    /**
     * Builds a transaction response object.
     *
     * @param transaction Completed transaction
     * @param senderUser Sender's user information
     * @param receiverUser Receiver's user information
     * @return Detailed transaction response
     */
    private TransactionResponse buildTransactionResponse(Transaction transaction,
                                                         UserResponse senderUser,
                                                         UserResponse receiverUser) {
        return TransactionResponse.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transaction.getSenderWalletCode())
                .senderMail(senderUser.getEmail())
                .recipientWalletCode(transaction.getRecipientWalletCode())
                .recipientMail(receiverUser.getEmail())
                .amount(transaction.getAmount())
                .status(String.valueOf(Status.SUCCESS))
                .description(transaction.getDescription())
                .build();
    }

    /**
     * Sends transaction notification via Kafka.
     *
     * @param senderWallet Wallet of the sender
     * @param result Transaction response details
     */
    private void sendTransactionNotification(WalletResponse senderWallet, TransactionResponse result) throws JsonProcessingException {
        EmailTransactionRequest emailTransactionRequest = buildEmailTransactionRequest(
                senderWallet.getUserId(),
                result,
                KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION
        );

        log.info("Sending transaction notification: {}", emailTransactionRequest);
        kafkaProducer.sendMessage(
                KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION,
                emailTransactionRequest
        );
    }

    /**
     * Builds an email transaction request.
     *
     * @param userId User identifier
     * @param data Transaction or OTP data
     * @param topicName Kafka topic name
     * @return Constructed EmailTransactionRequest
     */
    private EmailTransactionRequest buildEmailTransactionRequest(String userId, Object data, String topicName) {
        EmailTransactionRequest emailDTO = new EmailTransactionRequest();
        emailDTO.setUserId(userId);
        emailDTO.setData(gson.toJson(data));
        emailDTO.setTopicName(topicName);
        return emailDTO;
    }

    /**
     * Validates the One-Time Password (OTP).
     *
     * @param email User's email address
     * @param otpInput OTP input by the user
     * @throws BadRequestAlertException If OTP is invalid
     */
    private void validateOTP(String email, String otpInput) {
        String storedOtp = redisTemplate.opsForValue().get(email);

        if (otpInput == null) {
            throw new BadRequestAlertException(MessageCode.MSG4114);
        }

        if (storedOtp == null) {
            throw new BadRequestAlertException(MessageCode.MSG4113);
        }

        if (!storedOtp.equals(otpInput)) {
            throw new BadRequestAlertException(MessageCode.MSG4112);
        }
    }
    @Override
    public List<TransactionSearchResponse> getTransactionByInformation(TransactionSearch transactionSearch) {
        List<Transaction> transactionList = this.transactionRepository.findByTransactionIdOrRecipientWalletCodeOrSenderWalletCodeOrStatus(transactionSearch.getTransactionId(),transactionSearch.getWalletCode(),transactionSearch.getWalletCode(),transactionSearch.getStatus());
        List<TransactionSearchResponse> transactionResponseList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getCreatedDate().isBefore(transactionSearch.getToDate().toInstant()) &&
                    transaction.getCreatedDate().isAfter(transactionSearch.getFromDate().toInstant())) {
                WalletResponse walletResponse = walletClient.getWalletByWalletCode(transaction.getRecipientWalletCode());
                UserResponse userResponse = userClient.getUserById(walletResponse.getWalletCode());
                String fullName = userResponse.getFirstName() + " " + userResponse.getLastName();
                TransactionSearchResponse transactionSearchResponse = new TransactionSearchResponse(transaction.getTransactionCode(),transaction.getSenderWalletCode(),fullName,transaction.getRecipientWalletCode(),transaction.getAmount(),transaction.getDescription(),transaction.getStatus());
                transactionResponseList.add(transactionSearchResponse);
            }
        }
        return transactionResponseList;
    }
}
