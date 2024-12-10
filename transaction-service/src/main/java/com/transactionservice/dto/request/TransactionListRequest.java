package com.transactionservice.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class TransactionListRequest implements Serializable {
    private String walletCodeByUserLogIn;
    private String walletCodeByUserSearch;
    private String transactionCode;
    private String status;
    private Instant fromDate;
    private Instant toDate;
    private Integer page;
    private Integer size;
}
