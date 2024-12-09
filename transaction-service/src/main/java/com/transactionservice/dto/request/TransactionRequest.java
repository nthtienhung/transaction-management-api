package com.transactionservice.dto.request;

import com.transactionservice.dto.request.common.Request;
import com.transactionservice.enums.Status;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest extends Request {

    private String senderWalletCode;

    private String recipientWalletCode;

    private Long amount;

    private String description;
}
