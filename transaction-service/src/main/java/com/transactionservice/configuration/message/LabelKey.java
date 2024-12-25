package com.transactionservice.configuration.message;

public interface LabelKey {
    public static final String SUCCESS_CREATE_TRANSACTION = "success.create-transaction";
    public static final String SUCCESS_SEND_OTP = "success.send-otp";

    public static final String ERROR_ACCOUNT_NOT_EXITS = "error.account-not-exits";
    public static final String ERROR_RECIPIENT_REQUIRED = "error.recipient-required";
    public static final String ERROR_AMOUNT_REQUIRED = "error.amount-required";
    public static final String ERROR_INSUFFICIENT_BALANCE = "error.insufficient-balance";
    public static final String ERROR_NOT_FOUND = "error.not-found";
    public static final String ERROR_WALLET_CODE_NOT_SAME = "error.wallet-code-not-same";
    public static final String ERROR_AMOUNT_MUST_BE_POSITIVE = "error.amount-must-be-positive";

    public static final String OTP_INVALID_SO_BLOCKED_TRANSACTION = "otp.invalid-so-blocked-transaction";
    public static final String OTP_INVALID = "otp.invalid";
    public static final String OTP_EXPIRED = "otp.expired";
    public static final String OTP_EMPTY = "otp.empty";
}
