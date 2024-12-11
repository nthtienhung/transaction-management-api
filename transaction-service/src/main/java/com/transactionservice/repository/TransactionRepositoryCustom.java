package com.transactionservice.repository;

import com.transactionservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface TransactionRepositoryCustom {

    Page<Transaction> findFilteredTransactions(
            String walletCodeByUserLogIn,
            String walletCodeByUserSearch,
            String transactionCode,
            String status,
            Instant fromDate,
            Instant toDate,
            Pageable pageable
    );
}
