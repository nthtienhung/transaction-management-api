package com.transactionservice.dto.response.transaction;

import lombok.Data;

import java.time.Instant;

/**
 * Author: thinhtd
 * Date: 12/11/2024
 * Time: 5:23 PM
 */
@Data
public class TransactionDashboardResponse {

    private Long amount;

    private String transactionCode;

    private Instant createdDate;

}
