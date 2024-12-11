package com.transactionservice.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Instant;

@Data
public class TransactionListRequest implements Serializable {
    private String walletCodeByUserLogIn;
    private String walletCodeByUserSearch;
    private String transactionCode;
    private String status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant fromDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant toDate;
    private Integer page;
    private Integer size;
}
