package com.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.request.TransactionSearch;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.dto.response.TransactionSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getRecentReceivedTransactionListByUser();

    List<TransactionResponse> getRecentSentTransactionListByUser();
    
    TransactionResponse createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException;
    Page<TransactionSearchResponse> getTransactionByInformation(TransactionSearch transactionSearch, Pageable pageable);

}
