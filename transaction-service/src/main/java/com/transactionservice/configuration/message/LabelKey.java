package com.transactionservice.configuration.message;

public interface LabelKey {

    public static final String SUCCESS_CREATE_TRANSACTION = "success.create-transaction";
    public static final String SUCCESS_SEND_OTP = "success.send-otp";


    public static final String ERROR_RECIPIENT_REQUIRED = "error.recipient-required";
    public static final String ERROR_AMOUNT_REQUIRED = "error.amount-required";
    public static final String ERROR_INSUFFICIENT_BALANCE = "error.insufficient-balance";
    public static final String ERROR_NOT_FOUND = "error.not-found";

}
