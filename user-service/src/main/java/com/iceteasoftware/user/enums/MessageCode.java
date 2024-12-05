package com.iceteasoftware.user.enums;

import com.iceteasoftware.user.configuration.message.LabelKey;
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

    // Full name không đúng địng dạng
    MSG1009(LabelKey.ERROR_FULL_NAME_IS_INVALID),

    // Full name không để trống
    MSG1010(LabelKey.ERROR_FULL_NAME_IS_EMPTY),

    // Date of birth không trống
    MSG1011(LabelKey.ERROR_DATE_OF_BIRTH_IS_EMPTY),

    // Tài khoản đã tồn tại
    MSG1012(LabelKey.ERROR_ACCOUNT_DOES_EXIST),

    // Date of birth sai định dạng yyyy-MM-dd
    MSG1013(LabelKey.ERROR_DATE_OF_BIRTH_IS_INVALID),

    // Sever bị gián đoạn
    MSG1014(LabelKey.ERROR_INTERNAL_SERVER),

    // TOTP không để trống
    MSG1015(LabelKey.ERROR_TOTP_NOT_EMPTY),

    // Tài khoản không tồn tại
    MSG1016(LabelKey.ERROR_ACCOUNT_NOT_EXITS),

    // Mã TOTP không đúng hoặc đã hết hạn
    MSG1017(LabelKey.ERROR_TOTP_IS_INCORRECT_OR_HAS_EXPIRED),

    // TOTP không đúng định dạng
    MSG1018(LabelKey.ERROR_TOTP_IS_INVALID),

    // Date of birth không lớn hơn ngày hiện tại,
    // ngày sinh nhỏ hơn hoặc bằng 150 và lớn hơn 30 ngày
    MSG1019(LabelKey.ERROR_DOB_GREATER_THAN_NOW),

    // ID user không để trống
    MSG1020(LabelKey.ERROR_ID_USER_NOT_EMPTY),

    // Tài khoản đã active
    MSG1021(LabelKey.ERROR_ACCOUNT_IS_ACTIVE),

    MSG1022(LabelKey.ERROR_ID_USER_NOT_EMPTY),

    // TOTP không để trống
    MSG1023(LabelKey.ERROR_TOTP_NOT_EMPTY),

    // TOTP không đúng định dạng
    MSG1024(LabelKey.ERROR_TOTP_IS_INVALID),

    // QR Code TOTP bị gián đoạn
    MSG1025(LabelKey.ERROR_INTERNAL_SERVER),

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

    // Đăng nhập thành công
    MSG1034(LabelKey.SUCCESS_LOGIN_SUCCESS),

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

    // Người dùng vào thẳng API thiếu key
    MSG1042(LabelKey.ERROR_ILLEGAL_ACCESS_TO_API),

    //Nhập sai OTP quá 5 lần
    MSG1043(LabelKey.ERROR_INPUT_WRONG_OTP_MORE_THAN_FIVE_TIMES),

    // Phone number sai định dạng
    MSG1044(LabelKey.ERROR_PHONE_NUMBER_IS_INVALID),

    // Phone number để trống
    MSG1045(LabelKey.ERROR_PHONE_NUMBER_IS_EMPTY),

    // Người dùng chưa đăng nhập
    MSG1046(LabelKey.ERROR_USER_IS_NOT_LOGIN),

    // Success message
    MSG1047(LabelKey.SUCCESS_DEFAULT),

    // Success create message
    MSG1048(LabelKey.SUCCESS_CREATE),

    // Not have permission
    MSG1049(LabelKey.ERROR_YOU_HAVE_NOT_PERMISSION),

    //Invalid address
    MSG1050(LabelKey.ERROR_INVALID_ADDRESS),

    // Invalid first name
    MSG1051(LabelKey.ERROR_FIRST_NAME_IS_INVALID),

    // Invalid last name
    MSG1052(LabelKey.ERROR_LAST_NAME_IS_INVALID),

    MSG1053(LabelKey.ERROR_FIRST_NAME_IS_EMPTY),

    MSG1054(LabelKey.ERROR_LAST_NAME_IS_EMPTY),

    MSG1055(LabelKey.ERROR_PHONE_NUMBER_IS_USED),

    MSG1056(LabelKey.ERROR_TOKEN_IS_MISSING),

    MSG1057(LabelKey.ERROR_UNABLE_TO_DECODE_EMAIL_FROM_TOKEN),

    MSG1058(LabelKey.SUCCESS_RETRIEVED_PROFILE),

    MSG1059(LabelKey.ERROR_PROFILE_IS_NOT_FOUND),

    MSG1060(LabelKey.ERROR_ERROR_WHILE_RUNNING),

    MSG1061(LabelKey.SUCCESS_UPDATE_PROFILE),
    ;

    private String key;
}
