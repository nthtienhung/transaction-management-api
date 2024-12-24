package com.iceteasoftware.user.configuration.message;

public interface LabelKey {


    public static final String ERROR_DOB_GREATER_THAN_NOW = "error.dob-greater-than-now";
    public static final String ERROR_INVALID_EMAIL_OR_PASSWORD = "error.invalid-email-or-password";
    public static final String ERROR_INVALID_TOKEN = "error.invalid-token";
    public static final String ERROR_TOKEN_MALFORMED = "error.token-malformed";
    public static final String ERROR_TOKEN_HAS_EXPIRED = "error.token-has-expired";
    public static final String ERROR_JWT_TOKEN_IS_UNSUPPORTED = "error.jwt-token-is-unsupported";
    public static final String ERROR_PHONE_NUMBER_IS_INVALID = "error.phone-number-is-invalid";
    public static final String ERROR_PHONE_NUMBER_IS_EMPTY = "error.phone-number-is-empty";
    public static final String ERROR_FIRST_NAME_IS_EMPTY = "error.first-name-is-empty";
    public static final String ERROR_LAST_NAME_IS_EMPTY = "error.first-name-is-empty";
    public static final String ERROR_PHONE_NUMBER_IS_USED = "error.phone-number-is-used";
    public static final String ERROR_TOKEN_IS_MISSING = "error.token-is-missing";
    public static final String ERROR_UNABLE_TO_DECODE_EMAIL_FROM_TOKEN = "error.unable-to-decode-email-from-token";
    public static final String SUCCESS_RETRIEVED_PROFILE = "success.retrieved-profile";
    public static final String ERROR_PROFILE_IS_NOT_FOUND = "error.profile-is-not-found";
    public static final String ERROR_ERROR_WHILE_RUNNING = "error.error-while-running";
    public static final String SUCCESS_UPDATE_PROFILE = "success.update-profile";

}
