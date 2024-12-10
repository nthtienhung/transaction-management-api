package com.transactionservice.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Author: thinhtd
 * Date: 12/9/2024
 * Time: 3:21 PM
 */

@Getter
@Setter
public class ResponseObject<T> {

    private String message;
    private int status;
    private LocalDateTime localDateTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    // GET
    public ResponseObject(int status, String message, LocalDateTime localDateTime, T data) {
        this.status = status;
        this.message = message;
        this.localDateTime = localDateTime;
        this.data = data;
    }

    // UPDATE, DELETE, CREATE
    public ResponseObject(int status, String message, LocalDateTime localDateTime) {
        this.message = message;
        this.status = status;
        this.localDateTime = localDateTime;
    }

}
