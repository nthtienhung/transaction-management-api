package com.example.iamservice.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ResponseObject <T>{
    private String message;
    private int status;
    private LocalDateTime localDateTime;
    private T data;

    public ResponseObject(String message, int status, LocalDateTime localDateTime) {
        this.status = status;
        this.message = message;
        this.localDateTime = localDateTime;
    }

    public ResponseObject(String message, int status, LocalDateTime localDateTime, T data) {
        this.message = message;
        this.status = status;
        this.localDateTime = localDateTime;
        this.data = data;
    }

}
