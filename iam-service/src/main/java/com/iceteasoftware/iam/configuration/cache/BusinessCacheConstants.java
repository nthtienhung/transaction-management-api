package com.iceteasoftware.iam.configuration.cache;

import com.iceteasoftware.iam.constant.CacheConstants;

public interface BusinessCacheConstants extends CacheConstants {

    public interface User {
        public static final String FIND_BY_EMAIL = "user_findByEmail";

        public static final String OTP = "otp";

    }
}
