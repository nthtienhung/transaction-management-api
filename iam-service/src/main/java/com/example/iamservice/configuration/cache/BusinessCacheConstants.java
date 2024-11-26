package com.example.iamservice.configuration.cache;

import com.example.iamservice.constant.CacheConstants;

public interface BusinessCacheConstants extends CacheConstants {

    public interface User {
        public static final String FIND_BY_EMAIL = "user_findByEmail";

        public static final String OTP = "otp";

    }
}
