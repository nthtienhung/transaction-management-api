package com.transactionservice.configuration.cache;

import com.transactionservice.constant.CacheConstants;

public interface BusinessCacheConstants extends CacheConstants {

    public interface User {
        public static final String FIND_BY_EMAIL = "user_findByEmail";

        public static final String OTP = "otp";

    }
}
