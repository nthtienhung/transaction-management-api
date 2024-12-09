package com.transactionservice.dto.request.email;

import lombok.Data;

@Data
public class EmailTransactionRequest {

    private String userId;

    private String topicName;

    private String data;
}
