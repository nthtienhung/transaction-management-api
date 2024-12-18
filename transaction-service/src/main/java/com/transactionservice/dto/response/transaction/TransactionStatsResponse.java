package com.transactionservice.dto.response.transaction;

import com.transactionservice.enums.Status;
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
    private Status status;
    private Instant createdDate;
}
