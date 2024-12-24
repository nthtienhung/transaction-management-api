package com.iceteasoftware.iam.enums;

import com.iceteasoftware.iam.configuration.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCode {
    // Password không để trống
    MSG1001(LabelKey.ERROR_PASSWORD_IS_EMPTY),

    // Email không đúng định dạng
    MSG1002(LabelKey.ERROR_EMAIL_IS_INVALID),

    // Email không để trống
    MSG1003(LabelKey.ERROR_EMAIL_IS_EMPTY),

    // Password không đúng định dạng
    MSG1004(LabelKey.ERROR_PASSWORD_IS_INVALID),

    // Tài khoản hoặc mật khẩu không chính xác
    MSG1005(LabelKey.ERROR_EMAIL_OR_PASS_WORD_IS_WRONG),

    // Tài khoản đã bị khóa
    MSG1006(LabelKey.ERROR_ACCOUNT_IS_BLOCK),

    // Tài khoản bị tạm khóa sau 5 lần đăng nhập sai
    MSG1007(LabelKey.ERROR_YOUR_ACCOUNT_HAS_BEEN_TEMPORARILY_LOCKED),

    // Tài khoản chưa được ACTIVE
    MSG1008(LabelKey.ERROR_ACCOUNT_IS_INACTIVE),

    // Tài khoản đã tồn tại
    MSG1012(LabelKey.ERROR_ACCOUNT_DOES_EXIST),


    // Sever bị gián đoạn
    MSG1014(LabelKey.ERROR_INTERNAL_SERVER),


    // Tài khoản không tồn tại
    MSG1016(LabelKey.ERROR_ACCOUNT_NOT_EXITS),

    // Mã TOTP không đúng hoặc đã hết hạn
    MSG1017(LabelKey.ERROR_TOTP_IS_INCORRECT_OR_HAS_EXPIRED),

    // OTP không đúng định dạng
    MSG1026(LabelKey.ERROR_OTP_IS_INVALID),

    // OTP không được để trống
    MSG1027(LabelKey.ERROR_OTP_NOT_EMPTY),

    // OTP không khớp với email
    MSG1028(LabelKey.ERROR_OTP_IS_INVALID_FOR_EMAIL),

    // OTP đã hết hạn
    MSG1029(LabelKey.ERROR_OTP_HAS_EXPIRED),

    // OTP đã được sử dụng
    MSG1030(LabelKey.ERROR_OTP_ALREADY_USED),

    // Nhập lại mật khẩu không trùng khớp
    MSG1031(LabelKey.ERROR_PASSWORD_DOES_NOT_MATCH),

    // Yêu cầu nhập lại mật khẩu
    MSG1032(LabelKey.ERROR_PASSWORD_CONFIRM_NOT_EMPTY),

    // Mật khẩu mới trùng với mật khẩu cũ
    MSG1033(LabelKey.ERROR_NEW_PASSWORD_DUPLICATE),

    MSG1101(LabelKey.ERROR_ID_USER_NOT_EMPTY),

    // Email không tồn tại
    MSG1035(LabelKey.ERROR_EMAIL_NOT_EXIST),

    // Mật khẩu cũ không để trống
    MSG1036(LabelKey.ERROR_OLD_PASSWORD_IS_EMPTY),

    // Mật khẩu mới không để trống
    MSG1037(LabelKey.ERROR_NEW_PASSWORD_IS_EMPTY),

    // Mật khẩu xác nhận không để trống
    MSG1038(LabelKey.ERROR_CONFIRM_PASSWORD_IS_EMPTY),

    // Mật khẩu cũ sai
    MSG1039(LabelKey.ERROR_OLD_PASSWORD_IS_WRONG),

    // Đổi mật khẩu thành công
    MSG1040(LabelKey.SUCCESS_CHANGE_PASSWORD_SUCCESS),

    // Đăng xuất thành công
    MSG1041(LabelKey.SUCCESS_LOGOUT_SUCCESS),

    //Nhập sai OTP quá 5 lần
    MSG1043(LabelKey.ERROR_INPUT_WRONG_OTP_MORE_THAN_FIVE_TIMES),

    // Phone number sai định dạng
    MSG1044(LabelKey.ERROR_PHONE_NUMBER_IS_INVALID),

    // Phone number để trống
    MSG1045(LabelKey.ERROR_PHONE_NUMBER_IS_EMPTY),

    // Not have permission
    MSG1049(LabelKey.ERROR_YOU_HAVE_NOT_PERMISSION),

    //Invalid address
    MSG1050(LabelKey.ERROR_INVALID_ADDRESS),

    // Invalid first name
    MSG1051(LabelKey.ERROR_FIRST_NAME_IS_INVALID),

    // Invalid last name
    MSG1052(LabelKey.ERROR_LAST_NAME_IS_INVALID),

    MSG1053(LabelKey.EROR_FIRST_NAME_IS_EMPTY),

    MSG1054(LabelKey.EROR_LAST_NAME_IS_EMPTY),

    MSG1055(LabelKey.ERROR_PHONE_NUMBER_IS_USED),

    MSG1056(LabelKey.ERROR_USER_IS_REGISTER_BUT_NOT_VERIFIED);

    private String key;
}
