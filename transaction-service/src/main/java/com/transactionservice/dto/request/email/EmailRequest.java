package com.transactionservice.dto.request.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {
    private String walletCode;
    private String amount;
}
