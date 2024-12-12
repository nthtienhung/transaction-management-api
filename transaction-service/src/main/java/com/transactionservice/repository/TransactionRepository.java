package com.transactionservice.repository;

import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.Status;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "(COALESCE(:transactionId, '') = '' OR t.transactionId = :transactionId) AND " +
            "(COALESCE(:recipientWalletCode, '') = '' OR t.recipientWalletCode = :recipientWalletCode) AND " +
            "(COALESCE(:senderWalletCode, '') = '' OR t.senderWalletCode = :senderWalletCode)")
    Page<Transaction> findTransactions(
            @Param("transactionId") String transactionId,
            @Param("recipientWalletCode") String recipientWalletCode,
            @Param("senderWalletCode") String senderWalletCode,
            Pageable pageable);
}
