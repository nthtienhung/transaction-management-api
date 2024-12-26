package com.transactionservice.dto.response.transaction;


import lombok.Builder;
import lombok.Data;


import java.time.Instant;

@Data
@Builder
public class TransactionListResponse {
    private String transactionCode;
    private String senderWalletCode;
    private String receiverWalletCode;
    private Long amount;
    private String status;
    private String description;
    private String FirstName;
    private String LastName;
    private String FirstNameSender;
    private String LastNameSender;
    private Instant createdAt;
}