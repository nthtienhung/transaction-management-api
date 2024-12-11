package com.transactionservice.constant;

public interface Constants {

    public static final int DEFAULT_USER_ID_ADMIN = 1;
    public static final int DEFAULT_BIG_DECIMAL_ZERO = 0;
    public static final int DEFAULT_SIZE_LIST = 0;
    public static final String DEFAULT_FORMAT_DATE = "yyyyMMddHHmmss";
    public static final String DEFAULT_LOCALE = "locale";
    public static final String DEFAULT_REGEX_PHONE = "^(03|05|07|08|09)(\\d{8})$";
    public static final String DEFAULT_REGEXP_EMAIL = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,35})$";
    public static final String DEFAULT_MESSAGE_SUCCESS = "Success";
    public static final String DEFAULT_MESSAGE_SEND_OTP = "OTP has been sent to your email.";
    public static final String DEFAULT_MESSAGE_SUCCESS_TOTP = "OTP verify successfully!";
    public static final String DEFAULT_MESSAGE_CREATE_SUCCESS = "Created successfully";
    public static final String DEFAULT_MESSAGE_UPDATE_SUCCESS = "Updated successfully";

}
