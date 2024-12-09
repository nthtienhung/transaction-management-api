package com.transactionservice.service;

import com.transactionservice.dto.request.TransactionListRequest;
import com.transactionservice.dto.response.TransactionListResponse;
import com.transactionservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    Page<TransactionListResponse> getTransactions(TransactionListRequest request);
}
