package com.iceteasoftware.user.enums;

import com.iceteasoftware.user.configuration.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCode {

    // Date of birth không lớn hơn ngày hiện tại,
    // ngày sinh nhỏ hơn hoặc bằng 150 và lớn hơn 30 ngày
    MSG1019(LabelKey.ERROR_DOB_GREATER_THAN_NOW),

    // Phone number sai định dạng
    MSG1044(LabelKey.ERROR_PHONE_NUMBER_IS_INVALID),

    // Phone number để trống
    MSG1045(LabelKey.ERROR_PHONE_NUMBER_IS_EMPTY),

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
