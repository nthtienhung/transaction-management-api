package com.iceteasoftware.wallet.enums;

import com.iceteasoftware.wallet.configuration.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCode {

    MSG4100(LabelKey.ERROR_ACCOUNT_NOT_EXITS);

    private String key;
}
