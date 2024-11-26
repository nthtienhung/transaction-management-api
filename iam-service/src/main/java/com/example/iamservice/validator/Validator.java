package com.example.iamservice.validator;
import com.example.iamservice.constant.RegularExpressionConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class Validator {

    /**
     * Kiểm tra xem email có đúng định dạng hay không.
     *
     * @param email Chuỗi cần kiểm tra.
     * @return true nếu đối tượng là sai định dạng, false nếu ngược lại.
     */
    public static boolean isEmail(String email) {
        return !Pattern.compile(RegularExpressionConstants.DEFAULT_REGEXP_EMAIL)
                .matcher(email)
                .matches();
    }

    /**
     * Kiểm tra xem password có đúng định dạng hay không.
     *
     * @param password Chuỗi cần kiểm tra.
     * @return true nếu đối tượng là sai định dạng, false nếu ngược lại.
     */
    public static boolean isPassword(String password) {
        return !Pattern.compile(RegularExpressionConstants.DEFAULT_REGEXP_PASSWORD)
                .matcher(password)
                .matches();
    }

    /**
     * Kiểm tra xem password có đúng định dạng hay không.
     *
     * @param totp Chuỗi cần kiểm tra.
     * @return true nếu đối tượng là sai định dạng, false nếu ngược lại.
     */
    public static boolean isTOTP(String totp) {
        return !Pattern.compile(RegularExpressionConstants.DEFAULT_REGEXP_TOTP)
                .matcher(totp)
                .matches();
    }

    /**
     * Kiểm tra xem chuỗi cho có rỗng hoặc trống không.
     *
     * @param s Chuỗi cần kiểm tra.
     * @return true nếu đối tượng là null hoặc rỗng, false nếu ngược lại.
     */
    public static boolean isNull(String s) {
        return StringUtils.isBlank(s) || StringUtils.isEmpty(s);
    }
}

