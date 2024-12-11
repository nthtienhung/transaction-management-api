package com.transactionservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.transactionservice.client.UserClient;
import com.transactionservice.client.WalletClient;
import com.transactionservice.configuration.auditing.AuditorAwareConfig;
import com.transactionservice.configuration.kafka.KafkaProducer;
import com.transactionservice.constant.KafkaTopicConstants;
import com.transactionservice.dto.request.TransactionListRequest;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.request.TransactionSearch;
import com.transactionservice.dto.request.email.EmailTransactionRequest;
import com.transactionservice.dto.response.*;
import com.transactionservice.dto.response.TransactionSearchResponse;
import com.transactionservice.dto.response.UserResponse;
import com.transactionservice.dto.response.WalletResponse;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.MessageCode;
import com.transactionservice.enums.Status;
import com.transactionservice.exception.handler.BadRequestAlertException;
import com.transactionservice.exception.handler.NotFoundAlertException;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.repository.TransactionRepositoryCustom;
import com.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    private UserClient userClient;
    private WalletClient walletClient;
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

        walletClient.updateWalletBalance(senderWallet.getWalletCode(), transactionRequest.getAmount());
        walletClient.updateWalletBalance(receiverWallet.getWalletCode(), transactionRequest.getAmount());

        // Save Transaction to Database
        transactionRepository.save(transaction);

        com.transactionservice.dto.response.TransactionResponse result = com.transactionservice.dto.response.TransactionResponse.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transactionRequest.getSenderWalletCode())
                .senderMail(senderUser.getEmail())
                .recipientWalletCode(transactionRequest.getRecipientWalletCode())
                .recipientMail(receiverUser.getEmail())
                .amount(transactionRequest.getAmount())
                .status(String.valueOf(Status.SUCCESS))
                .description(transactionRequest.getDescription())
                .build();

        System.out.println("Transaction Result: " + result);

        String userId = new AuditorAwareConfig().getCurrentAuditor().get();

        EmailTransactionRequest emailTransactionRequest = new EmailTransactionRequest();
        emailTransactionRequest.setUserId(userId);
        emailTransactionRequest.setTopicName(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION);
        emailTransactionRequest.setData(new Gson().toJson(result));

        System.out.println("Email Transaction Request: " + emailTransactionRequest);

        // Send Notification via Kafka
        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION, emailTransactionRequest);

        // Build Response Object
        return result;
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

    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundAlertException(MessageCode.MSG4111));
    }

    private String generateTransactionCode() {
        // Generate unique transaction code
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    }
}
