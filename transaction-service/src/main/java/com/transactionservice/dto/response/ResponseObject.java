package com.transactionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject <T>{
    private String message;
    private int status;
    private LocalDateTime localDateTime;
    private T data;

    public ResponseObject(int status, String message, LocalDateTime localDateTime) {
        this.status = status;
        this.message = message;
        this.localDateTime = localDateTime;
    }
}
