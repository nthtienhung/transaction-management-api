package com.transactionservice.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionListRequest {
    private String walletCode;
    private String transactionCode;
    private String status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
