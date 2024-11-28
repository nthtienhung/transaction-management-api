package com.example.iamservice.constant;

public class RegularExpressionConstants {

    public static final String DEFAULT_REGEXP_EMAIL = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@(gmail|GMAIL)$*(\\.com|\\.COM)$";
    public static final String DEFAULT_REGEXP_PASSWORD = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*_+])[A-Za-z\\d!@#$%^&*_+]{8,}$";
    public static final String DEFAULT_REGEXP_TOTP = "[0-9]+";
    public static final String DEFAULT_REGEXP_FULL_NAME = "^[\\p{L}\\s]+( [\\p{L}\\s]+)*$";
    public static final String DEFAULT_REGEXP_PHONE_NUMBER = "(+84|0[3|5|7|8|9])+([0-9]{8})\\\\b";


}
