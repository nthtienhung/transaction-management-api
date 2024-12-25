package com.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.dto.request.ConfirmTransactionRequest;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.request.email.EmailRequest;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.dto.request.TransactionListRequest;
import com.transactionservice.dto.response.TransactionDashboardResponse;
import com.transactionservice.dto.response.TransactionListResponse;
import org.springframework.data.domain.Page;
import com.transactionservice.dto.request.TransactionSearch;
import com.transactionservice.dto.response.TransactionSearchResponse;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    Page<TransactionDashboardResponse> getRecentReceivedTransactionListByUser(String walletByUserLogIn);

    Page<TransactionDashboardResponse> getRecentSentTransactionListByUser(String walletByUserLogIn);

    Page<TransactionListResponse> getTransactionListByUser(TransactionListRequest request);

    TransactionResponse createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException;

    Page<TransactionSearchResponse> getTransactionByInformation(TransactionSearch transactionSearch, Pageable pageable);

    double getTotalSentTransactionByUserInWeek(String senderWalletCode);

    double getTotalReceivedTransactionByUserInWeek(String recipientWalletCode);

    void generateOtp(EmailRequest request) throws JsonProcessingException;

    TransactionResponse initiateTransaction(ConfirmTransactionRequest confirmTransactionRequest) throws JsonProcessingException;

    TransactionResponse confirmTransactionWithOTP(ConfirmTransactionRequest confirmTransactionRequest) throws JsonProcessingException;

    Integer getTotalTransactionByUser(String walletCode);
}

