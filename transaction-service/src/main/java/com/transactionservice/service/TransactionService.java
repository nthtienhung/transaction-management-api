package com.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.request.TransactionSearch;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.dto.response.TransactionSearchResponse;

import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getRecentReceivedTransactionListByUser();

    List<TransactionResponse> getRecentSentTransactionListByUser();
    
    TransactionResponse createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException;
    List<TransactionSearchResponse> getTransactionByInformation(TransactionSearch transactionSearch);

}
