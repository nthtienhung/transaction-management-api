package com.transactionservice.repository;

import com.transactionservice.dto.response.transaction.TransactionStatsResponse;
import com.transactionservice.entity.Transaction;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "(COALESCE(:transactionCode, '') = '' OR t.transactionCode = :transactionCode) AND " +
            "(COALESCE(:recipientWalletCode, '') = '' OR t.recipientWalletCode = :recipientWalletCode) OR " +
            "(COALESCE(:senderWalletCode, '') = '' OR t.senderWalletCode = :senderWalletCode)")
    Page<Transaction> findTransactions(
            @Param("transactionCode") String transactionCode,
            @Param("recipientWalletCode") String recipientWalletCode,
            @Param("senderWalletCode") String senderWalletCode,
            Pageable pageable);

    @Query("SELECT t.amount, t.createdDate, t.transactionCode FROM Transaction t " +
            "WHERE t.recipientWalletCode = :recipientWalletCode")
    Page<Object[]> findRecentReceivedTransaction(String recipientWalletCode, Pageable pageable);

    @Query("SELECT t.amount, t.createdDate, t.transactionCode FROM Transaction t " +
            "WHERE t.senderWalletCode = :senderWalletCode")
    Page<Object[]> findRecentSentTransaction(String senderWalletCode, Pageable pageable);


    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.recipientWalletCode = :recipientWalletCode " +
            "AND t.createdDate BETWEEN :startDate AND :endDate")
    Double sumRecentReceivedTransactions(String recipientWalletCode, Instant startDate, Instant endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.senderWalletCode = :senderWalletCode " +
            "AND t.createdDate BETWEEN :startDate AND :endDate")
    Double sumRecentSentTransactions(String senderWalletCode, Instant startDate, Instant endDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.senderWalletCode = :walletCode OR t.recipientWalletCode = :walletCode")
    Integer countBySenderWalletCodeOrRecipientWalletCode(String walletCode);

    @Query("SELECT t " +
            "FROM Transaction t " +
            "WHERE t.transactionCode = :transactionCode")
    Transaction findTransactionByAdmin(String transactionCode);

    @Query("SELECT t " +
            "FROM Transaction t " +
            "WHERE (t.senderWalletCode = :walletCode OR t.recipientWalletCode = :walletCode) " +
            "AND t.transactionCode = :transactionCode ")
    Transaction findTransactionDetailByUser(String transactionCode, String walletCode);

    @Query("SELECT COUNT(t), SUM(t.amount) " +
            "FROM Transaction t WHERE t.createdDate BETWEEN :startDate AND :endDate")
    List<Object[]> getTransactionStatistics(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT t.senderWalletCode, COUNT(t), SUM(t.amount) " +
            "FROM Transaction t WHERE t.createdDate BETWEEN :startDate AND :endDate " +
            "GROUP BY t.senderWalletCode")
    List<Object[]> getUserTransactionStatistics(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT t.transactionCode, t.senderWalletCode, t.recipientWalletCode, t.amount, t.status, t.createdDate " +
            "FROM Transaction t WHERE t.createdDate BETWEEN :startDate AND :endDate")
    List<Object[]> getTransactionDetails(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("SELECT new com.transactionservice.dto.response.transaction.TransactionStatsResponse(t.transactionCode, t.senderWalletCode, t.recipientWalletCode, t.amount, t.status, t.createdDate) " +
            "FROM Transaction t " +
            "WHERE t.createdDate BETWEEN :startDate AND :endDate")
    List<TransactionStatsResponse> getTransactions(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

}
