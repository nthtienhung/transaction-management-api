package com.transactionservice.service.TransactionServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.transactionservice.client.UserClient;
import com.transactionservice.client.WalletClient;
import com.transactionservice.configuration.auditing.AuditorAwareConfig;
import com.transactionservice.configuration.kafka.KafkaProducer;
import com.transactionservice.constant.KafkaTopicConstants;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.request.email.EmailTransactionRequest;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.dto.response.UserResponse;
import com.transactionservice.dto.response.WalletResponse;
import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.MessageCode;
import com.transactionservice.enums.Status;
import com.transactionservice.exception.handler.BadRequestAlertException;
import com.transactionservice.exception.handler.NotFoundAlertException;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserClient userClient;
    private final WalletClient walletClient;
    private final KafkaProducer kafkaProducer;



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

    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundAlertException(MessageCode.MSG4111));
    }

    private String generateTransactionCode() {
        // Generate unique transaction code
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
