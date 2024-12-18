package com.iceteasoftware.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatsResponse {
    private String transactionCode;
    private String senderWalletCode;
    private String recipientWalletCode;
    private Long amount;
    private String status;
    private Instant createdDate;
}
