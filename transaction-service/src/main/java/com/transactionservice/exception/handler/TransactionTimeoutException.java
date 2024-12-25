package com.transactionservice.exception.handler;

public class TransactionTimeoutException extends RuntimeException {
    public TransactionTimeoutException(String message) {
        super(message);
    }
}
