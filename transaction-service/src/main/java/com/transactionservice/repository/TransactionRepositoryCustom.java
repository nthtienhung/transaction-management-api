package com.transactionservice.repository;

import com.transactionservice.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepositoryCustom {

    List<Transaction> findFilteredTransactions(
            String walletCode,
            String transactionCode,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );
}
