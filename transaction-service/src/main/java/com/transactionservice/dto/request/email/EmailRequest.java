package com.transactionservice.dto.request.email;

import lombok.Data;

@Data
public class EmailRequest {
    private String walletCode;
    private String amount;
}
