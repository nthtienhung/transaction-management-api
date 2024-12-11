package com.transactionservice.configuration.message;

public interface LabelKey {
    public static final String ERROR_INTERNAL_SERVER = "error.internal-server";
    public static final String ERROR_BAB_REQUEST = "error.bab-request";
    public static final String ERROR_EMAIL_IS_INVALID = "error.email-is-invalid";
    public static final String ERROR_EMAIL_NOT_EXIST = "error.email-not-exist";
    public static final String ERROR_EMAIL_IS_EMPTY = "error.email-is-empty";
    public static final String ERROR_PHONE_IS_EMPTY = "error.phone-is-empty";
    public static final String ERROR_PHONE_IS_INVALID = "error.phone-is-invalid";
    public static final String ERROR_PAYMENT_METHOD_IS_EMPTY = "error.payment-method-is-empty";
    public static final String ERROR_PAYMENT_METHOD_NOT_EXIST = "error.payment-method-not-exist";
    public static final String ERROR_PAYMENT_SOURCE_IS_EMPTY = "error.payment-source-is-empty";
    public static final String ERROR_PAYMENT_SOURCE_NOT_EXIST = "error.payment-source-not-exist";
    public static final String ERROR_STATUS_IS_EMPTY = "error.status-is-empty";
    public static final String ERROR_STATUS_NOT_EXIST = "error.status-not-exist";
    public static final String ERROR_AMOUNT_IS_EMPTY = "error.amount-is-empty";
    public static final String ERROR_AMOUNT_MUST_BE_GREATER_THAN_ZERO = "error.amount-must-be-greater-than-zero";
    public static final String ERROR_NOT_DATA = "error.not-data";
    public static final String SUCCESS_DEFAULT = "success.default";
    public static final String SUCCESS_CREATE = "success.create-success";
    public static final String ERROR_TRANSACTION_NOT_EXIST = "error.transaction-not-exist";
    public static final String SUCCESS_CREATE_TRANSACTION = "success.create-transaction";
    public static final String SUCCESS_SEND_OTP = "success.send-otp";

    public static final String ERROR_ACCOUNT_NOT_EXITS = "error.account-not-exits";
    public static final String ERROR_RECIPIENT_REQUIRED = "error.recipient-required";
    public static final String ERROR_AMOUNT_REQUIRED = "error.amount-required";
    public static final String ERROR_INSUFFICIENT_BALANCE = "error.insufficient-balance";
    public static final String ERROR_NOT_FOUND = "error.not-found";

    public static final String OTP_INVALID = "otp.invalid";
    public static final String OTP_EXPIRED = "otp.expired";
    public static final String OTP_EMPTY = "otp.empty";
}
