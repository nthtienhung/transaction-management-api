package com.example.iamservice.exception;

import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.exception.handler.BadRequestAlertException;
import com.example.iamservice.exception.handler.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.time.LocalDateTime;


@Slf4j
@RestControllerAdvice
public class ExceptionTranslator implements ProblemHandling {
    @ExceptionHandler(BadRequestAlertException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseObject<String> handleBadRequestAlertException(BadRequestAlertException ex) {
        return new ResponseObject<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    }
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseObject<String> handleInternalServerErrorException(InternalServerErrorException ex) {
        return new ResponseObject<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    }

}
