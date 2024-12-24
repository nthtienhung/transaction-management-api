package com.iceteasoftware.common.enums;

import com.iceteasoftware.common.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public enum MessageCode {

    // Sever bị gián đoạn
    MSG1014(LabelKey.ERROR_INTERNAL_SERVER),

    // Success message
    MSG1047(LabelKey.SUCCESS_DEFAULT);


    private String key;

    MessageCode() {

    }

    MessageCode(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
