package com.iceteasoftware.config.enums;

import com.iceteasoftware.config.configuration.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCode {

    // Success Messages
    MSG2001(LabelKey.SUCCESS_CREATE_CONFIG),
    MSG2002(LabelKey.SUCCESS_UPDATE_CONFIG),
    MSG2003(LabelKey.SUCCESS_GET_CONFIG),
    MSG2004(LabelKey.SUCCESS_SOFT_DELETE_CONFIG),

    // Error Messages
    MSG2101(LabelKey.ERROR_GROUP_REQUIRED),
    MSG2102(LabelKey.ERROR_TYPE_REQUIRED),
    MSG2103(LabelKey.ERROR_KEY_REQUIRED),
    MSG2104(LabelKey.ERROR_VALUE_REQUIRED),
    MSG2105(LabelKey.ERROR_UPDATE_DB),
    MSG2106(LabelKey.ERROR_INSERT_DB),
    MSG2107(LabelKey.ERROR_CONFIG_NOT_FOUND),
    MSG2108(LabelKey.ERROR_QUERY_DB),
    MSG2109(LabelKey.ERROR_CONFIG_ID_NOT_FOUND),
    MSG2110(LabelKey.ERROR_DELETE_DB),
    MSG2111(LabelKey.ERROR_EXIST_CONFIG);

    private String key;
}
