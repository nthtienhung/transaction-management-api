package com.transactionservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.transactionservice.client.UserClient;
import com.transactionservice.client.WalletClient;
import com.transactionservice.configuration.kafka.KafkaConsumerService;
import com.transactionservice.configuration.kafka.KafkaProducer;
import com.transactionservice.constant.KafkaTopicConstants;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.request.TransactionSearch;
import com.transactionservice.dto.request.*;
import com.transactionservice.dto.request.email.EmailRequest;
import com.transactionservice.dto.request.email.EmailTransactionRequest;
import com.transactionservice.dto.response.*;
import com.transactionservice.dto.response.wallet.WalletResponse;
import com.transactionservice.dto.response.wallet.WalletTransactionStatus;
import com.transactionservice.dto.response.FullNameResponse;
import com.transactionservice.dto.response.transaction.*;
import com.transactionservice.dto.response.user.UserResponse;
import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.MessageCode;
import com.transactionservice.enums.Stage;
import com.transactionservice.enums.Status;
import com.transactionservice.exception.handler.BadRequestAlertException;
import com.transactionservice.exception.handler.NotFoundAlertException;
import com.transactionservice.exception.handler.TransactionTimeoutException;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.repository.TransactionRepositoryCustom;
import com.transactionservice.service.TransactionService;
import com.iceteasoftware.common.util.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 0;
    private final Gson gson;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaConsumerService kafkaConsumerService;

    @Override
    public Page<TransactionDashboardResponse> getRecentReceivedTransactionListByUser(String walletCodeByUserLogIn) {
        return mapTransactionsToDashboardResponse(
                transactionRepository.findRecentReceivedTransaction(walletCodeByUserLogIn, createPageable())
        );
    }

    @Override
    public Page<TransactionDashboardResponse> getRecentSentTransactionListByUser(String walletCodeByUserLogIn) {
        return mapTransactionsToDashboardResponse(
                transactionRepository.findRecentSentTransaction(walletCodeByUserLogIn, createPageable())
        );
    }

    private Page<TransactionDashboardResponse> mapTransactionsToDashboardResponse(Page<Object[]> transactionsPage) {
        return transactionsPage.map(objects -> {
            TransactionDashboardResponse response = new TransactionDashboardResponse();
            response.setAmount((Long) objects[0]); // Mapping the amount
            response.setCreatedDate((Instant) objects[1]); // Mapping the createdDate
            response.setTransactionCode((String) objects[2]); // Mapping the transactionCode
            return response;
        });
    }

    private Pageable createPageable() {
        return PageRequest.of(PAGE_NUMBER, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdDate"));
    }

    @Override
    public Page<TransactionListResponse> getTransactionListByUser(TransactionListRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Instant startOfDay = null;
        Instant endOfDay = null;

        if (request.getFromDate() != null) {
            startOfDay = request.getFromDate().atZone(ZoneId.of("UTC")).toInstant();
        }

        if (request.getToDate() != null) {
            endOfDay = request.getToDate().atZone(ZoneId.of("UTC")).with(LocalTime.MAX).toInstant();
        }
        Page<Transaction> transactionsPage = transactionRepositoryCustom.findFilteredTransactions(
                request.getWalletCodeByUserLogIn(),
                request.getWalletCodeByUserSearch(),
                request.getTransactionCode(),
                request.getStatus(),
                startOfDay,
                endOfDay,
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
        String senderWalletCode = transaction.getSenderWalletCode();

        // Lấy userId từ recipientWalletCode
        String userId = walletClient.getUserIdByWalletCode(recipientWalletCode);

        // Lấy thông tin user (firstName, lastName) từ senderWalletCode
        String userIdSender = walletClient.getUserIdByWalletCode(senderWalletCode);

        // Lấy thông tin user (firstName, lastName) từ userId
        FullNameResponse fullNameResponse = userClient.getFullNameByUserId(userId);
        FullNameResponse fullNameResponseSender = userClient.getFullNameByUserId(userIdSender);

        return TransactionListResponse.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transaction.getSenderWalletCode())
                .receiverWalletCode(transaction.getRecipientWalletCode())
                .amount(transaction.getAmount())
                .status(String.valueOf(transaction.getStatus()))
                .description(transaction.getDescription())
                .FirstName(fullNameResponse.getFirstName())
                .LastName(fullNameResponse.getLastName())
                .FirstNameSender(fullNameResponseSender.getFirstName())
                .LastNameSender(fullNameResponseSender.getLastName())
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

        // Reset số lần nhập sai OTP nếu có
        String attemptsKey = "otp_attempts:" + user.getEmail();
        redisTemplate.delete(attemptsKey);

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

    @Override
    public TransactionResponse initiateTransaction(ConfirmTransactionRequest confirmTransactionRequest) {

        if (confirmTransactionRequest.getRecipientWalletCode().isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG4101);
        }

        if (confirmTransactionRequest.getAmount() == null) {
            throw new BadRequestAlertException(MessageCode.MSG4102);
        }

        if (confirmTransactionRequest.getSenderWalletCode().equals(confirmTransactionRequest.getRecipientWalletCode())) {
            throw new BadRequestAlertException(MessageCode.MSG4115);
        }

        if (confirmTransactionRequest.getAmount() <= 0) {
            throw new BadRequestAlertException(MessageCode.MSG4116);
        }

        WalletResponse senderWallet = walletClient.getWalletByWalletCode(confirmTransactionRequest.getSenderWalletCode());
        UserResponse senderUser = userClient.getUserById(senderWallet.getUserId());

        validateOTP(senderUser.getEmail(), confirmTransactionRequest.getOtp());

        ThreadLocalUtil.setCurrentUser(senderWallet.getUserId());

        Transaction transaction = createPendingTransaction(confirmTransactionRequest);

        redisTemplate.opsForValue().set(transaction.getTransactionCode(), String.valueOf(transaction.getStage()));

        try {
            // Step 1: Debit Phase
            WalletTransactionStatus deductedStatus = processDebitPhase(transaction);
            transaction.setStage(deductedStatus.getStage());
            transaction.setErrorMessage(deductedStatus.getErrorMessage());
            transactionRepository.save(transaction);
            redisTemplate.opsForValue().set(transaction.getTransactionCode(), String.valueOf(transaction.getStage()));
            log.info("Received deducted status: {}", deductedStatus);
            if (deductedStatus.getStage() != Stage.DEDUCTED) {
                handleFailure(transaction, Stage.CREATED, deductedStatus.getErrorMessage());
                throw new TransactionException("Debit failed: " + deductedStatus.getErrorMessage());
            }

            // Step 2: Credit Phase
            WalletTransactionStatus creditStatus = processCreditPhase(deductedStatus);
            transaction.setStage(creditStatus.getStage());
            transaction.setErrorMessage(creditStatus.getErrorMessage());
            transactionRepository.save(transaction);
            redisTemplate.opsForValue().set(transaction.getTransactionCode(), String.valueOf(transaction.getStage()));
            log.info("Received credited status: {}", creditStatus);
            if (creditStatus.getStage() != Stage.CREDITED) {
                handleFailure(transaction, Stage.DEDUCTED, creditStatus.getErrorMessage());
                throw new TransactionException("Credit failed: " + creditStatus.getErrorMessage());
            }

            return handleSuccessTransaction(transaction);

        } catch (TransactionTimeoutException e) {
            handleTimeout(transaction);
            throw new TransactionException("Transaction timed out");
        } catch (Exception e) {
            handleFailure(transaction, transaction.getStage(), e.getMessage());
            throw new TransactionException("Transaction failed: " + e.getMessage());
        } finally {
            ThreadLocalUtil.remove();
        }
    }

    private WalletTransactionStatus processDebitPhase(Transaction transaction) {
        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_TRANSACTION,
                buildUpdateWalletRequest(transaction));

        return kafkaConsumerService.listenForResponse(
                transaction.getTransactionCode(),
                Arrays.asList(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SUCCESSFUL_DEDUCT_WALLET, "TRANSACTION_FAILED"),
                30
        );
    }

    private WalletTransactionStatus processCreditPhase(WalletTransactionStatus transaction) {
        UpdateWalletRequest updateWalletRequest = UpdateWalletRequest.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transaction.getSenderWalletCode())
                .recipientWalletCode(transaction.getRecipientWalletCode())
                .stage(Stage.DEDUCTED)
                .amount(transaction.getAmount())
                .build();
        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREDIT_WALLET, updateWalletRequest);

        return kafkaConsumerService.listenForResponse(
                    transaction.getTransactionCode(),
                Arrays.asList(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SUCCESSFUL_CREDIT_WALLET, "TRANSACTION_FAILED"),
                30
        );
    }

    private void handleTimeout(Transaction transaction) {
        transaction.setStatus(Status.FAILED);
        transaction.setErrorMessage("Transaction timed out");
        transactionRepository.save(transaction);
        redisTemplate.opsForValue().set(transaction.getTransactionCode(), String.valueOf(Stage.COMPENSATED));

        // Trigger compensation based on current stage
        if (transaction.getStage() == Stage.DEDUCTED) {
            log.info("Sending DEDUCTED compensation message for transaction: {}", transaction.getTransactionCode());
            kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_COMPENSATION,
                    buildUpdateWalletRequest(transaction));
        } else if (transaction.getStage() == Stage.CREDITED) {
            log.info("Sending CREDITED compensation message for transaction: {}", transaction.getTransactionCode());
            kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_COMPENSATION,
                    buildUpdateWalletRequest(transaction));
        }
    }

    private UpdateWalletRequest buildUpdateWalletRequest(Transaction transaction) {
        return UpdateWalletRequest.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transaction.getSenderWalletCode())
                .recipientWalletCode(transaction.getRecipientWalletCode())
                .stage(transaction.getStage())
                .amount(transaction.getAmount())
                .build();
    }

    private TransactionResponse handleSuccessTransaction(Transaction transaction) throws JsonProcessingException {
        log.info("Transaction completed: {}", transaction.getTransactionCode());
        transaction.setStatus(Status.SUCCESS);
        transaction.setStage(Stage.COMPLETED);
        transactionRepository.save(transaction);
        redisTemplate.delete(transaction.getTransactionCode());

        TransactionResponse result = TransactionResponse.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transaction.getSenderWalletCode())
                .senderMail(userClient.getUserById(walletClient.getUserIdByWalletCode(transaction.getSenderWalletCode())).getEmail())
                .recipientWalletCode(transaction.getRecipientWalletCode())
                .recipientMail(userClient.getUserById(walletClient.getUserIdByWalletCode(transaction.getRecipientWalletCode())).getEmail())
                .amount(transaction.getAmount())
                .status(String.valueOf(Status.SUCCESS))
                .description(transaction.getDescription())
                .build();

        sendTransactionNotification(
                walletClient.getWalletByWalletCode(transaction.getSenderWalletCode()),
                result
        );

        return result;
    }

    private void handleFailure(Transaction transaction, Stage errorState, String errorMessage) {
        transaction.setStatus(Status.FAILED);
        transaction.setErrorMessage("Error : " + errorMessage);
        transactionRepository.save(transaction);
        redisTemplate.opsForValue().set(transaction.getTransactionCode(), String.valueOf(Stage.COMPENSATED));

        // Gửi message bồi hoàn
        if (Stage.DEDUCTED == errorState) {
            log.info("Sending compensation compensation message for transaction: {}", transaction.getTransactionCode());
            kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_COMPENSATION, new UpdateWalletRequest(transaction.getTransactionCode(), transaction.getSenderWalletCode(), transaction.getRecipientWalletCode(), Stage.DEDUCTED, transaction.getAmount()));
        } else if (Stage.COMPLETED == errorState) {
            log.info("Sending compensation message for transaction: {}", transaction.getTransactionCode());
            kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_COMPENSATION, new UpdateWalletRequest(transaction.getTransactionCode(), transaction.getSenderWalletCode(), transaction.getRecipientWalletCode(), Stage.COMPENSATED, transaction.getAmount()));
        }
    }

    private Transaction createPendingTransaction(ConfirmTransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionCode(generateTransactionCode())
                .senderWalletCode(request.getSenderWalletCode())
                .recipientWalletCode(request.getRecipientWalletCode())
                .amount(request.getAmount())
                .status(Status.PENDING)
                .description(request.getDescription())
                .stage(Stage.CREATED)
                .build();
        transactionRepository.save(transaction);
        return transaction;
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

    @Override
    public Integer getTotalTransactionByUser(String walletCode) {
        return transactionRepository.countBySenderWalletCodeOrRecipientWalletCode(walletCode);
    }

    @Override
    public TransactionDetailResponse getTransactionDetailByAdmin(String transactionCode) {
        // Fetch transaction details
        Transaction transaction = transactionRepository.findTransactionByAdmin(transactionCode);
        if (transaction == null) {
            throw new NotFoundAlertException(MessageCode.MSG4111);
        }

        // Fetch sender and recipient details
        return buildTransactionDetailResponse(transaction);
    }

    @Override
    public TransactionDetailResponse getTransactionDetailByUser(String transactionCode) {
        // Retrieve username and associated wallet code
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userClient.getUserIdByUsername(username);
        ResponseEntity<WalletResponse> wallet = walletClient.getWallet(userId);

        // Fetch transaction details
        Transaction transaction = transactionRepository.findTransactionDetailByUser(transactionCode, Objects.requireNonNull(wallet.getBody()).getWalletCode());
        if (transaction == null) {
            throw new NotFoundAlertException(MessageCode.MSG4111);
        }

        // Fetch sender and recipient details
        return buildTransactionDetailResponse(transaction);
    }

    /**
     * Helper method to build TransactionDetailResponse from a Transaction object.
     */
    private TransactionDetailResponse buildTransactionDetailResponse(Transaction transaction) {
        // Fetch sender's full name
        String senderId = walletClient.getUserIdByWalletCode(transaction.getSenderWalletCode());
        FullNameResponse senderFullNameResponse = userClient.getFullNameByUserId(senderId);

        // Fetch recipient's full name
        String recipientId = walletClient.getUserIdByWalletCode(transaction.getRecipientWalletCode());
        FullNameResponse recipientFullNameResponse = userClient.getFullNameByUserId(recipientId);

        // Construct response object
        TransactionDetailResponse response = new TransactionDetailResponse();
        response.setTransactionCode(transaction.getTransactionCode());
        response.setSenderWalletCode(transaction.getSenderWalletCode());
        response.setRecipientWalletCode(transaction.getRecipientWalletCode());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setNameOfSender(senderFullNameResponse.getFirstName() + " " + senderFullNameResponse.getLastName());
        response.setNameOfRecipient(recipientFullNameResponse.getFirstName() + " " + recipientFullNameResponse.getLastName());
        response.setStatus(String.valueOf(transaction.getStatus()));
        response.setCreatedDate(transaction.getCreatedDate());
        response.setUpdatedDate(transaction.getUpdatedDate());

        return response;
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
     * @param senderWallet       Sender's wallet information
     * @param receiverWallet     Receiver's wallet information
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
     * @param senderWallet   Wallet of the sender
     * @param receiverWallet Wallet of the receiver
     * @param amount         Transaction amount
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
     * @param transaction  Completed transaction
     * @param senderUser   Sender's user information
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
     * @param result       Transaction response details
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
     * @param userId    User identifier
     * @param data      Transaction or OTP data
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
     * @param email    User's email address
     * @param otpInput OTP input by the user
     * @throws BadRequestAlertException If OTP is invalid
     */
    private void validateOTP(String email, String otpInput) {
        String storedOtp = redisTemplate.opsForValue().get(email);
        String attemptsKey = "otp_attempts:" + email;
        String blockKey = "block_transfer:" + email;

        // Kiểm tra xem người dùng có bị khóa hay không
        if (redisTemplate.hasKey(blockKey)) {
            throw new BadRequestAlertException(MessageCode.MSG4118); // "Bạn đã nhập sai OTP quá số lần cho phép. Vui lòng thử lại sau 1 phút."
        }

        // Kiểm tra nếu OTP đầu vào rỗng
        if (otpInput == null) {
            throw new BadRequestAlertException(MessageCode.MSG4114); // "OTP không được để trống."
        }

        // Kiểm tra nếu OTP đã hết hạn
        if (storedOtp == null) {
            throw new BadRequestAlertException(MessageCode.MSG4113); // "OTP đã hết hạn. Vui lòng yêu cầu OTP mới."
        }

        // Kiểm tra nếu OTP không đúng
        if (!storedOtp.equals(otpInput)) {
            // Tăng số lần nhập sai
            Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
            redisTemplate.expire(attemptsKey, 5, TimeUnit.MINUTES); // Đặt TTL cho số lần nhập sai là 5 phút

            if (attempts >= 5) {
                // Khóa giao dịch nếu sai OTP >= 5 lần
                redisTemplate.opsForValue().set(blockKey, "LOCKED", 1, TimeUnit.MINUTES); // Khóa trong 1 phút
                redisTemplate.delete(attemptsKey); // Xóa số lần nhập sai sau khi khóa
                redisTemplate.delete(email); // Xóa OTP sau khi khóa
                throw new BadRequestAlertException(MessageCode.MSG4118); // "Bạn đã nhập sai OTP quá số lần cho phép. Vui lòng thử lại sau 1 phút."
            }

            throw new BadRequestAlertException(MessageCode.MSG4112); // "OTP không chính xác."
        }

        // Nếu OTP đúng, reset số lần nhập sai
        redisTemplate.delete(attemptsKey);
    }

    @Override
    public Page<TransactionSearchResponse> getTransactionByInformation(TransactionSearch transactionSearch, Pageable pageable) {
        String transactionId = (transactionSearch.getTransactionId() != null && !transactionSearch.getTransactionId().isEmpty())
                ? transactionSearch.getTransactionId()
                : null;

        String walletCode = (transactionSearch.getWalletCode() != null && !transactionSearch.getWalletCode().isEmpty())
                ? transactionSearch.getWalletCode()
                : null;
        String sendWallet = walletCode;
        String receiverWallet = walletCode;

        // Tìm giao dịch từ repository
        Page<Transaction> transactionList = transactionRepository.findTransactions(transactionId, sendWallet, receiverWallet, pageable);
        List<TransactionSearchResponse> transactionResponseList = new ArrayList<>();

        // Kiểm tra điều kiện theo ngày
        for (Transaction transaction : transactionList) {
            boolean statusMatches = (transactionSearch.getStatus() == null || transactionSearch.getStatus().equals(transaction.getStatus()));

            // Kiểm tra nếu từ ngày hoặc đến ngày là null hoặc rỗng
            if (transactionSearch.getFromDate() == null || transactionSearch.getToDate() == null) {
                if (statusMatches) {
                    checkValueTransaction(transactionSearch, transaction, transactionResponseList);
                }
            } else if (transaction.getCreatedDate().isAfter(transactionSearch.getFromDate()) &&
                    transaction.getCreatedDate().isBefore(transactionSearch.getToDate())) {
                if (statusMatches) {
                    checkValueTransaction(transactionSearch, transaction, transactionResponseList);
                }
            }
        }

        return new PageImpl<>(transactionResponseList);
    }

    private void checkValueTransaction(TransactionSearch transactionSearch, Transaction transaction, List<TransactionSearchResponse> transactionResponseList) {
        String transactionId = transactionSearch.getTransactionId();
        String walletCode = transactionSearch.getWalletCode();
        Status status = transactionSearch.getStatus();
        Instant fromDate = transactionSearch.getFromDate();
        Instant toDate = transactionSearch.getToDate();

            boolean isFromDateEmpty = fromDate == null || transaction.getCreatedDate().isBefore(fromDate);
        boolean isToDateEmpty = toDate == null || transaction.getCreatedDate().isAfter(toDate);
        boolean isTransactionIdEmpty = transactionId == null || transactionId.isEmpty();
        boolean isWalletCodeEmpty = walletCode == null || walletCode.isEmpty();
        boolean isStatusEmpty = status == null;

        // Case 1: All filters are empty -> Include all transactions
        if (isTransactionIdEmpty && isWalletCodeEmpty && isStatusEmpty && isFromDateEmpty && isToDateEmpty) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2: Filters with specific combinations
        boolean matchesTransactionId = !isTransactionIdEmpty && transaction.getTransactionCode().equals(transactionId);
        boolean matchesWalletCode = !isWalletCodeEmpty &&
                (transaction.getRecipientWalletCode().equals(walletCode) || transaction.getSenderWalletCode().equals(walletCode));
        boolean matchesStatus = !isStatusEmpty && transaction.getStatus().equals(status);
        boolean matchesFromDate = !isFromDateEmpty && transaction.getCreatedDate().isBefore(fromDate);
        boolean matchesToDate = !isToDateEmpty && transaction.getCreatedDate().isAfter(toDate);

        // Case 2a: Matches all filters
        if (matchesTransactionId && matchesWalletCode && matchesStatus && matchesFromDate && matchesToDate) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2b: Matches transactionId only
        if (matchesTransactionId && isWalletCodeEmpty && isStatusEmpty && isFromDateEmpty && isToDateEmpty) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2c: Matches walletCode only
        if (matchesWalletCode && isTransactionIdEmpty && isStatusEmpty && isFromDateEmpty && isToDateEmpty) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2d: Matches status only
        if (matchesStatus && isTransactionIdEmpty && isWalletCodeEmpty && isFromDateEmpty && isToDateEmpty) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2e: Matches transactionId and walletCode
        if (matchesTransactionId && matchesWalletCode && isStatusEmpty && isFromDateEmpty && isToDateEmpty) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2f: Matches transactionId and status
        if (matchesTransactionId && matchesStatus && isWalletCodeEmpty && isFromDateEmpty && isToDateEmpty) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2g: Matches walletCode and status
        if (matchesWalletCode && matchesStatus && isTransactionIdEmpty && isFromDateEmpty && isToDateEmpty) {
            addTransaction(transaction, transactionResponseList);
            return;
        }

        // Case 2h: Matches fromDate and toDate only
            if (!isFromDateEmpty && !isToDateEmpty) {
                addTransaction(transaction, transactionResponseList);
                return;
            }



        // Case 2i: Matches combinations of filters including fromDate and toDate
        if ((matchesTransactionId || matchesWalletCode || matchesStatus) && matchesFromDate && matchesToDate) {
            addTransaction(transaction, transactionResponseList);
        }
    }
    private void addTransaction(Transaction transaction, List<TransactionSearchResponse> transactionResponseList) {
        WalletResponse walletResponse = walletClient.getWalletByWalletCode(transaction.getSenderWalletCode());
        WalletResponse walletResponseToUser = walletClient.getWalletByWalletCode(transaction.getRecipientWalletCode());
        UserResponse userResponse = userClient.getUserById(walletResponse.getUserId());
        UserResponse userResponseToUser = userClient.getUserById(walletResponseToUser.getUserId());
        String fullNameToUser = userResponseToUser.getFirstName() + " " + userResponseToUser.getLastName();
        String fullName = userResponse.getFirstName() + " " + userResponse.getLastName();
        TransactionSearchResponse transactionSearchResponse = new TransactionSearchResponse(transaction.getTransactionCode(), transaction.getSenderWalletCode(),fullName, transaction.getRecipientWalletCode(),fullNameToUser, transaction.getAmount(), transaction.getDescription(), transaction.getStatus());
        transactionResponseList.add(transactionSearchResponse);
    }


    @Override
    public double getTotalSentTransactionByUserInWeek(String senderWalletCode) {
        Instant[] weekRange = getCurrentWeekRange();
        return transactionRepository.sumRecentSentTransactions(senderWalletCode, weekRange[0], weekRange[1]);
    }

    @Override
    public double getTotalReceivedTransactionByUserInWeek(String recipientWalletCode) {
        Instant[] weekRange = getCurrentWeekRange();
        return transactionRepository.sumRecentReceivedTransactions(recipientWalletCode, weekRange[0], weekRange[1]);
    }


    private Instant[] getCurrentWeekRange() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Instant startDate = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();

        return new Instant[]{startDate, endDate};
    }

    @Override
    public Map<String, Object> getGeneralStatistics(Instant startDate, Instant endDate) {
        try {
            Object[] stats = transactionRepository.getTransactionStatistics(startDate, endDate).get(0);
            Map<String, Object> result = new HashMap<>();
            result.put("totalTransactions", stats[0]);
            result.put("totalAmount", stats[1]);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching general statistics: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getUserStatistics(Instant startDate, Instant endDate) {
        try {
            List<Object[]> userStats = transactionRepository.getUserTransactionStatistics(startDate, endDate);
            return userStats.stream().map(stat -> {
                Map<String, Object> userStat = new HashMap<>();
                userStat.put("senderWallet", stat[0]);
                userStat.put("totalTransactions", stat[1]);
                userStat.put("totalAmount", stat[2]);
                return userStat;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user statistics: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getTransactionDetails(Instant startDate, Instant endDate) {
        try {
            List<Object[]> transactions = transactionRepository.getTransactionDetails(startDate, endDate);
            return transactions.stream().map(transaction -> {
                Map<String, Object> result = new HashMap<>();
                result.put("transactionCode", transaction[0]);
                result.put("senderWalletCode", transaction[1]);
                result.put("recipientWalletCode", transaction[2]);
                result.put("amount", transaction[3]);
                result.put("status", transaction[4]);
                result.put("date", transaction[5]);
                return result;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching transaction details: " + e.getMessage());
        }
    }

    @Override
    public List<TransactionStatsResponse> getTransactions(Instant startDate, Instant endDate) {
        try {
            List<TransactionStatsResponse> transactions = transactionRepository.getTransactions(startDate, endDate);
            return transactions;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching transaction details: " + e.getMessage());
        }
    }
}
