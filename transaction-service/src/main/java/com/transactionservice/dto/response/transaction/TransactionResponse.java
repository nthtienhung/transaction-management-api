package com.transactionservice.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private String transactionCode;
    private String senderWalletCode;
    private String senderMail;
    private String recipientWalletCode;
    private String recipientMail;
    private Long amount;
    private String status;
    private String description;

}
