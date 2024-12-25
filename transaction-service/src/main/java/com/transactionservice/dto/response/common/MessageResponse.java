package com.transactionservice.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse<T>{

    private String message;
    private short status;
    private String localDateTime;
    private T data;

    public MessageResponse(short status, String message, LocalDateTime localDateTime, T data) {
        this.status = status;
        this.message = message;
        this.localDateTime = String.valueOf(localDateTime);
        this.data = data;
    }

    public MessageResponse(short status, String message, LocalDateTime localDateTime) {
        this.message = message;
        this.status =  status;
        this.localDateTime = String.valueOf(localDateTime);
    }
}
