package com.transactionservice.dto.request;

import com.transactionservice.enums.Status;

import java.time.Instant;

public class TransactionRequestRepository {
    private String transactionId;
    private String walletCode;
    private Status status;

    public TransactionRequestRepository(String transactionId, String walletCode, Status status) {
        this.transactionId = transactionId;
        this.walletCode = walletCode;
        this.status = status;
    }

    public TransactionRequestRepository() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionRequestRepository setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getWalletCode() {
        return walletCode;
    }

    public TransactionRequestRepository setWalletCode(String walletCode) {
        this.walletCode = walletCode;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public TransactionRequestRepository setStatus(Status status) {
        this.status = status;
        return this;
    }
}
