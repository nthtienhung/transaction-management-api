package com.transactionservice.repository;

import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> , JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByTransactionIdOrRecipientWalletCodeOrSenderWalletCodeOrStatus(String transactionId, String recipientWalletCode, String senderWalletCode, Status status);

}
