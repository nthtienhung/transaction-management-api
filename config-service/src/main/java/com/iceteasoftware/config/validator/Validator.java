package com.iceteasoftware.config.validator;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class Validator {

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

