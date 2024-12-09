package com.transactionservice.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class TransactionListResponse {
    private String transactionId;
    private String transactionCode;
    private String senderWalletId;
    private String recipientWalletId;
    private long amount;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
