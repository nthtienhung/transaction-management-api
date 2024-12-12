package com.transactionservice.dto.request;

import com.transactionservice.enums.Status;

import java.time.Instant;

public class TransactionSearch {
    private String transactionId;
    private String walletCode;
    private Status status;
    private Instant fromDate;
    private Instant toDate;

    public TransactionSearch(String transactionId, String walletCode, Status status, Instant fromDate, Instant toDate) {
        this.transactionId = transactionId;
        this.walletCode = walletCode;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionSearch setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getWalletCode() {
        return walletCode;
    }

    public TransactionSearch setWalletCode(String walletCode) {
        this.walletCode = walletCode;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public TransactionSearch setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Instant getFromDate() {
        return fromDate;
    }

    public TransactionSearch setFromDate(Instant fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public Instant getToDate() {
        return toDate;
    }

    public TransactionSearch setToDate(Instant toDate) {
        this.toDate = toDate;
        return this;
    }
}
