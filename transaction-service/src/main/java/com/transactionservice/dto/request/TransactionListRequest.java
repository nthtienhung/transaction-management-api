package com.transactionservice.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class TransactionListRequest {
    private String transactionCode;
    private String senderWalletId;
    private String recipientWalletId;
    private String status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private int page; // Số trang
    private int size; // Kích thước trang
}
