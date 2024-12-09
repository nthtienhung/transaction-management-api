package com.transactionservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionListResponse {
    private String transactionCode;
    private String senderWalletCode;
    private String receiverWalletCode;
    private Long amount;
    private String status;
    private String description;

    public TransactionListResponse(String transactionCode, String senderWalletCode, String receiverWalletCode, Long amount, String status, String description, LocalDateTime createdAt) {
    }
}