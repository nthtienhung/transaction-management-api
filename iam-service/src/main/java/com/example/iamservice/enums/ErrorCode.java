package com.example.iamservice.enums;


import com.example.iamservice.configuration.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
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

    // Date of birth sai định dạng YYYY-MM-DD
    MSG1013(LabelKey.ERROR_DATE_OF_BIRTH_IS_INVALID),

    // Sever bị gián đoạn
    MSG1014(LabelKey.ERROR_INTERNAL_SERVER),

    // TOTP không để trống
    MSG1015(LabelKey.ERROR_TOTP_NOT_EMPTY),

    // Mã TOTP không đúng hoặc đã hết hạn
    MSG1017(LabelKey.ERROR_TOTP_IS_INCORRECT_OR_HAS_EXPIRED),

    // TOTP không đúng định dạng
    MSG1018(LabelKey.ERROR_TOTP_IS_INVALID),

    // Date of birth không lớn hơn ngày hiện tại
    MSG1100(LabelKey.ERROR_DOB_GREATER_THAN_NOW),

    // ID user không để trống
    MSG1101(LabelKey.ERROR_ID_USER_NOT_EMPTY),
    ;

    private String key;
}

