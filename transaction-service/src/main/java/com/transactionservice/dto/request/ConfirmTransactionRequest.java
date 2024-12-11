package com.transactionservice.dto.request;

import lombok.Data;

@Data
public class ConfirmTransactionRequest {
    private String senderWalletCode;

    private String recipientWalletCode;

    private Long amount;

    private String description;

    private String otp;
}
