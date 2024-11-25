package com.example.iamservice.exception;

import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.exception.handler.BadRequestAlertException;
import com.example.iamservice.exception.handler.InternalServerErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionTranslator {

    /**
     * Xử lý BadRequestAlertException và
     * trả về một ResponseEntity với mã status HTTP 400 (Bad Request).
     *
     * @param ex Đối tượng BadRequestAlertException.
     * @return ResponseEntity chứa thông báo lỗi.
     */
    @ExceptionHandler(BadRequestAlertException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseObject<String> handleBadRequestAlertException(BadRequestAlertException ex) {
        return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now());
    }

    /**
     * Xử lý InternalServerErrorException và
     * trả về một ResponseEntity với mã status HTTP 500 (Internal Server Error).
     *
     * @param ex Đối tượng InternalServerErrorException.
     * @return ResponseEntity chứa thông báo lỗi.
     */
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseObject<String> handleInternalServerErrorException(InternalServerErrorException ex) {
        return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), LocalDateTime.now());
    }

}
