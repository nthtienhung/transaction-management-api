package com.transactionservice.enums;

import com.transactionservice.configuration.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCode {

    // Success Messages
    MSG4001(LabelKey.SUCCESS_CREATE_TRANSACTION),
    MSG4002(LabelKey.SUCCESS_SEND_OTP),

    // Error Messages
    // Tài khoản không tồn tại
    MSG4100(LabelKey.ERROR_ACCOUNT_NOT_EXITS),
    MSG4101(LabelKey.ERROR_RECIPIENT_REQUIRED),
    MSG4102(LabelKey.ERROR_AMOUNT_REQUIRED),
    MSG4103(LabelKey.ERROR_INSUFFICIENT_BALANCE),
    MSG4111(LabelKey.ERROR_NOT_FOUND),
    MSG4112(LabelKey.OTP_INVALID),
    MSG4113(LabelKey.OTP_EXPIRED),
    MSG4114(LabelKey.OTP_EMPTY);

    private String key;
}
