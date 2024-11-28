package com.example.iamservice.exception.forbidden;

import com.example.iamservice.dto.response.common.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseObject<String> handleCustomAccessDeniedException(CustomAccessDeniedException e) {
        return new ResponseObject<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), LocalDateTime.now());
    }

}
