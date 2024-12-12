package com.transactionservice.repository;

import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> , JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByTransactionIdOrRecipientWalletCodeOrSenderWalletCodeOrStatus(String transactionId, String recipientWalletCode, String senderWalletCode, Status status);

    @Query("SELECT t.amount, t.createdDate, t.transactionCode FROM Transaction t " +
            "WHERE t.recipientWalletCode = :recipientWalletCode")
    Page<Object[]> findRecentReceivedTransaction(String recipientWalletCode, Pageable pageable);

    @Query("SELECT t.amount, t.createdDate, t.transactionCode FROM Transaction t " +
            "WHERE t.senderWalletCode = :senderWalletCode")
    Page<Object[]> findRecentSentTransaction(String senderWalletCode, Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.recipientWalletCode = :recipientWalletCode " +
            "AND t.createdDate BETWEEN :startDate AND :endDate")
    Double sumRecentReceivedTransactions(String recipientWalletCode, Instant startDate, Instant endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.senderWalletCode = :senderWalletCode " +
            "AND t.createdDate BETWEEN :startDate AND :endDate")
    Double sumRecentSentTransactions(String senderWalletCode, Instant startDate, Instant endDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.senderWalletCode = :walletCode OR t.recipientWalletCode = :walletCode")
    Integer countBySenderWalletCodeOrRecipientWalletCode(String walletCode);
}
