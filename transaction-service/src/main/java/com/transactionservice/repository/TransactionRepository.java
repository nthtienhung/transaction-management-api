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

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> , JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByTransactionIdOrRecipientWalletCodeOrSenderWalletCodeOrStatus(String transactionId, String recipientWalletCode, String senderWalletCode, Status status);

    @Query("select t.amount, t.createdDate, t.transactionCode from Transaction t " +
            "where t.recipientWalletCode = :recipientWalletCode")
    Page<Object[]> findRecentReceivedTransaction(String recipientWalletCode, Pageable pageable);

    @Query("select t.amount, t.createdDate, t.transactionCode from Transaction t " +
            "where t.senderWalletCode = :senderWalletCode")
    Page<Object[]> findRecentSentTransaction(String senderWalletCode, Pageable pageable);



}
