package com.transactionservice.mapper;

import com.transactionservice.dto.response.TransactionListResponse;
import com.transactionservice.entity.Transaction;

public class TransactionMapper {
    public static TransactionListResponse toTransactionResponseDTO(Transaction transaction) {
        TransactionListResponse dto = new TransactionListResponse();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionCode(transaction.getTransactionCode());
        dto.setSenderWalletId(transaction.getSenderWalletId());
        dto.setRecipientWalletId(transaction.getReceiverWalletId());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus());
        dto.setDescription(transaction.getDescription());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}
