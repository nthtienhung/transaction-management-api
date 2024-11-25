package com.example.iamservice.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ACTIVE(0),
    INACTIVE(1),
    BLOCK(2);

    private int value;

    public static Status valueOfName(int value) {
        for (Status status : Status.values()) {
            if (status.getValue() == value) return status;
        }

        return null;
    }

}
