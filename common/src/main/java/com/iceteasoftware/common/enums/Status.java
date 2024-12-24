package com.iceteasoftware.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum Status {
    ACTIVE(0),
    INACTIVE(1),
    BLOCK(2);

    private int value;

    Status() {

    }

    Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Status valueOfName(int value) {
        for (Status status : Status.values()) {
            if (status.getValue() == value) return status;
        }

        return null;
    }

}
