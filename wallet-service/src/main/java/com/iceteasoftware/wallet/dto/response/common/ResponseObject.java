package com.iceteasoftware.wallet.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:31 AM
 */
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
