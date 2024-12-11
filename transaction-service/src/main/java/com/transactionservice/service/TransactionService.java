package com.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.dto.request.TransactionListRequest;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.response.TransactionListResponse;
import com.transactionservice.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getRecentReceivedTransactionListByUser();

    List<TransactionResponse> getRecentSentTransactionListByUser();
    
    Page<TransactionListResponse> getTransactionListByUser(TransactionListRequest request);

    TransactionResponse createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException;
}

