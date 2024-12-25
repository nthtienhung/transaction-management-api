package com.iceteasoftware.wallet.util;

import com.iceteasoftware.wallet.constant.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetterUtils {
    public static final String RETURN_NEW_LINE = "\r\n";

    public static final String NEW_LINE = "\n";
    public static final String BLANK = "";

    /**
     * Lấy giá trị chuỗi từ một chuỗi và cung cấp giá trị mặc định nếu chuỗi null.
     *
     * @param value        Giá trị chuỗi đầu vào
     * @param defaultValue Giá trị mặc định
     * @return Giá trị chuỗi hoặc giá trị mặc định nếu chuỗi đầu vào là null
     */
    public static String get(String value, String defaultValue) {
        if (value != null) {
            value = value.trim();
            value = replace(value, RETURN_NEW_LINE, NEW_LINE);

            return value;
        }

        return defaultValue;
    }
    /**
     * Lấy giá trị chuỗi từ một chuỗi và cung cấp giá trị mặc định nếu chuỗi null.
     *
     * @param value        Giá trị chuỗi đầu vào
     * @param defaultValue Giá trị mặc định
     * @return Giá trị chuỗi hoặc giá trị mặc định nếu chuỗi đầu vào là null
     */
    public static String getString(String value, String defaultValue) {
        return get(value, defaultValue);
    }


    /**
     * Thay thế tất cả các chuỗi con trong chuỗi đầu vào bằng chuỗi mới.
     *
     * @param s       Chuỗi đầu vào
     * @param oldSub  Chuỗi con cần thay thế
     * @param newSub  Chuỗi mới thay thế
     * @return Chuỗi kết quả sau khi thay thế
     */
    public static String replace(String s, String oldSub, String newSub) {
        return replace(s, oldSub, newSub, 0);
    }

    /**
     * Thay thế tất cả các chuỗi con trong chuỗi đầu vào bằng chuỗi mới từ vị trí chỉ định.
     *
     * @param s         Chuỗi đầu vào
     * @param oldSub    Chuỗi con cần thay thế
     * @param newSub    Chuỗi mới thay thế
     * @param fromIndex Vị trí bắt đầu thay thế
     * @return Chuỗi kết quả sau khi thay thế
     */
    public static String replace(String s, String oldSub, String newSub, int fromIndex) {

        if (s == null) {
            return null;
        }

        if (oldSub == null || oldSub.equals(BLANK)) {
            return s;
        }

        if (newSub == null) {
            newSub = BLANK;
        }

        int y = s.indexOf(oldSub, fromIndex);

        if (y >= 0) {
            StringBuilder sb = new StringBuilder();

            int length = oldSub.length();
            int x = 0;

            while (x <= y) {
                sb.append(s.substring(x, y));
                sb.append(newSub);

                x = y + length;
                y = s.indexOf(oldSub, x);
            }

            sb.append(s.substring(x));

            return sb.toString();
        } else {
            return s;
        }
    }

    /**
     * Phương thức sinh mã giao dịch từ thời gian hiện tại.
     *
     * @return Mã giao dịch được sinh ra từ thời gian hiện tại
     */
    public static String generateCode(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DEFAULT_FORMAT_DATE);
        return currentTime.format(formatter);
    }

}
