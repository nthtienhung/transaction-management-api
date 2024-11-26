package com.example.iamservice.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * The ResponseObject<T> is the class define response object when returned
 *
 * @author duongduc1520
 * @version 1.0
 * @since 2024-03-26
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject<T> {

    private String message;
    private int status;
    private LocalDateTime localDateTime;
    private T data;


    public ResponseObject(int status, String message, LocalDateTime localDateTime, T data) {
        this.status = status;
        this.message = message;
        this.localDateTime = localDateTime;
        this.data = data;
    }

    public ResponseObject(int status, String message, LocalDateTime localDateTime) {
        this.message = message;
        this.status = status;
        this.localDateTime = localDateTime;
    }

}
