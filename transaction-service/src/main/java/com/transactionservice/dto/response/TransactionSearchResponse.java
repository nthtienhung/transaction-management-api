package com.transactionservice.dto.response;

import com.transactionservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSearchResponse {
    private String transactionCode;
    private String senderWalletCode;
    private String fromUser;
    private String receiverWalletCode;
    private String toUser;
    private Long amount;
    private String description;
    private Status status;
}
