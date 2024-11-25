package com.example.iamservice.constant;

public interface GatewayCacheConstants extends CacheConstants {


    public interface Others {
        public static final String OTP = "otp";

        public static final String OTP_VERIFY = "otp-verify";

        public static final String PASSWORD_VERIFY = "password-verify";

        public static final String TEMP_FILE = "temp-file";
    }

    public interface UserLoginFailed {
        public static final String FIND_BY_USER_ID = "userLoginFailed_findByUserId";
    }

    public interface UserEkyc {
        public static final String FIND_BY_TRANSACTION_ID = "userEkyc_findByTransactionId";
    }
}


