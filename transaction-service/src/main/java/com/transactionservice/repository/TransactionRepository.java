package com.transactionservice.repository;

import com.transactionservice.entity.Transaction;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "(COALESCE(:transactionCode, '') = '' OR t.transactionCode = :transactionCode) AND " +
            "(COALESCE(:recipientWalletCode, '') = '' OR t.recipientWalletCode = :recipientWalletCode) AND " +
            "(COALESCE(:senderWalletCode, '') = '' OR t.senderWalletCode = :senderWalletCode)")
    Page<Transaction> findTransactions(
            @Param("transactionCode") String transactionCode,
            @Param("recipientWalletCode") String recipientWalletCode,
            @Param("senderWalletCode") String senderWalletCode,
            Pageable pageable);


    @Query("select t.amount, t.createdDate, t.transactionCode from Transaction t " +
            "where t.recipientWalletCode = :recipientWalletCode")
    Page<Object[]> findRecentReceivedTransaction(String recipientWalletCode, Pageable pageable);

    @Query("select t.amount, t.createdDate, t.transactionCode from Transaction t " +
            "where t.senderWalletCode = :senderWalletCode")
    Page<Object[]> findRecentSentTransaction(String senderWalletCode, Pageable pageable);
}
