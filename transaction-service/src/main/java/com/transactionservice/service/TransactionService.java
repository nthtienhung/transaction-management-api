package com.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.response.TransactionResponse;

public interface TransactionService {

    public TransactionResponse createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException;
}
