package com.example.iamservice.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject<T> {

    private String message;
    private int status;
    private LocalDateTime localDateTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseObject(int status, String message, LocalDateTime localDateTime) {
        this.status = status;
        this.message = message;
        this.localDateTime = localDateTime;
    }

}
