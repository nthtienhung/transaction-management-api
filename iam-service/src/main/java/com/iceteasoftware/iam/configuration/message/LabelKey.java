package com.iceteasoftware.iam.configuration.message;

public interface LabelKey {

    public static final String ERROR_ACCOUNT_DOES_EXIST = "error.account-does-exist";
    public static final String ERROR_TOTP_IS_INCORRECT_OR_HAS_EXPIRED = "error.otp-is-incorrect-or-has-expired";
    public static final String ERROR_EMAIL_IS_EMPTY = "error.email-is-empty";
    public static final String ERROR_EMAIL_IS_INVALID = "error.email-is-invalid";
    public static final String ERROR_PASSWORD_IS_EMPTY = "error.password-is-empty";
    public static final String ERROR_PASSWORD_IS_INVALID = "error.password-is-invalid";
    public static final String ERROR_ID_USER_NOT_EMPTY = "error.id-user-not-empty";
    public static final String ERROR_INTERNAL_SERVER = "error.internal-server";
    public static final String ERROR_OTP_NOT_EMPTY = "error.otp-not-empty";
    public static final String ERROR_OTP_IS_INVALID = "error.otp-is-invalid";
    public static final String ERROR_OTP_ALREADY_USED = "error.otp-is-already-used";
    public static final String ERROR_OTP_HAS_EXPIRED = "error.otp-has-expired";
    public static final String ERROR_PASSWORD_CONFIRM_NOT_EMPTY = "error.password-confirm-not-empty";
    public static final String ERROR_OTP_IS_INVALID_FOR_EMAIL = "error.otp-is-invalid-for-email";
    public static final String ERROR_PASSWORD_DOES_NOT_MATCH = "error.password-do-does-not-match";
    public static final String ERROR_NEW_PASSWORD_DUPLICATE = "error.new-password-duplicate";
    public static final String ERROR_INVALID_EMAIL_OR_PASSWORD = "error.invalid-email-or-password";
    public static final String ERROR_INVALID_TOKEN = "error.invalid-token";
    public static final String ERROR_TOKEN_MALFORMED = "error.token-malformed";
    public static final String ERROR_TOKEN_HAS_EXPIRED = "error.token-has-expired";
    public static final String ERROR_JWT_TOKEN_IS_UNSUPPORTED = "error.jwt-token-is-unsupported";
    public static final String ERROR_ACCOUNT_IS_BLOCK = "error.account-has-been-blocked";
    public static final String ERROR_ACCOUNT_IS_INACTIVE = "error.account-is-inactive";
    public static final String ERROR_EMAIL_OR_PASS_WORD_IS_WRONG = "error.email-or-password-is-wrong";
    public static final String ERROR_YOUR_ACCOUNT_HAS_BEEN_TEMPORARILY_LOCKED = "error.your-account-has-been-temporarily-locked";
    public static final String ERROR_ACCOUNT_NOT_EXITS = "error.account-not-exits";
    public static final String ERROR_INPUT_WRONG_OTP_MORE_THAN_FIVE_TIMES = "error.input-wrong-otp-more-than-five-times";
    public static final String ERROR_EMAIL_NOT_EXIST = "error.email-not-exist";
    public static final String ERROR_OLD_PASSWORD_IS_EMPTY = "error.old-password-is-empty";
    public static final String ERROR_NEW_PASSWORD_IS_EMPTY = "error.new-password-is-empty";
    public static final String ERROR_CONFIRM_PASSWORD_IS_EMPTY = "error.confirm-password-is-empty";
    public static final String ERROR_OLD_PASSWORD_IS_WRONG = "error.old-password-is-wrong";
    public static final String ERROR_PHONE_NUMBER_IS_INVALID = "error.phone-number-is-invalid";
    public static final String ERROR_PHONE_NUMBER_IS_EMPTY = "error.phone-number-is-empty";
    public static final String ERROR_YOU_HAVE_NOT_PERMISSION = "error.you-have-no-permission";
    public static final String SUCCESS_CHANGE_PASSWORD_SUCCESS = "success.change-password-success";
    public static final String SUCCESS_LOGOUT_SUCCESS = "success.logout-success";
    public static final String ERROR_INVALID_ADDRESS = "error.invalid-address";
    public static final String ERROR_FIRST_NAME_IS_INVALID = "error.invalid-first-name";
    public static final String ERROR_LAST_NAME_IS_INVALID = "error.invalid-last-name";
    public static final String EROR_FIRST_NAME_IS_EMPTY = "eror.first-name-is-empty";
    public static final String EROR_LAST_NAME_IS_EMPTY = "eror.first-name-is-empty";
    public static final String ERROR_PHONE_NUMBER_IS_USED = "error.phone-number-is-used";
    public static final String ERROR_USER_IS_REGISTER_BUT_NOT_VERIFIED = "error.user-is-register-but-not-verified";
    public static final String ERROR_REFRESH_TOKEN_EXPIRED = "error.refresh-token-expired";
}
