package com.transactionservice.controller;

import com.transactionservice.dto.request.ConfirmTransactionRequest;
import com.transactionservice.dto.request.email.EmailRequest;
import com.transactionservice.dto.response.common.ResponseObject;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.constant.Constants;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.response.MessageResponse;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {


    private final TransactionService transactionService;

    @GetMapping("/recent-received-transaction-list-by-user")
    public MessageResponse<List<TransactionResponse>> getRecentReceivedTransactionList() {
        List<TransactionResponse> data = transactionService.getRecentReceivedTransactionListByUser();
        return new MessageResponse<>((short)HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @GetMapping("/recent-sent-transaction-list-by-user")
    public MessageResponse<List<TransactionResponse>> getRecentSentTransactionList() {
        List<TransactionResponse> data = transactionService.getRecentSentTransactionListByUser();
        return new MessageResponse<>((short)HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

//    @PostMapping("/create-transaction")
//    public MessageResponse<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) throws JsonProcessingException {
//        TransactionResponse savedTransaction = transactionService.createTransaction(transactionRequest);
//        return MessageResponse.<TransactionResponse>builder()
//                .status((short) HttpStatus.CREATED.value())
//                .message(Constants.DEFAULT_MESSAGE_SUCCESS)
//                .data(savedTransaction)
//                .localDateTime(String.valueOf(Instant.now()))
//                .build();
//    }

    /**
     * API gửi OTP xác nhận giao dịch
     */
    @PostMapping("/send-otp")
    public MessageResponse<String> sendTransactionOTP(@RequestBody EmailRequest sendOtpRequest) throws JsonProcessingException {
        transactionService.generateOtp(sendOtpRequest);
        return MessageResponse.<String>builder()
                .status((short) HttpStatus.OK.value())
                .message(Constants.DEFAULT_MESSAGE_SEND_OTP)
                .localDateTime(String.valueOf(Instant.now()))
                .build();
    }

    /**
     * API xác nhận giao dịch với OTP
     */
    @PostMapping("/confirm")
    public MessageResponse<TransactionResponse> confirmTransactionWithOTP(@RequestBody ConfirmTransactionRequest confirmTransactionRequest) throws JsonProcessingException {
        TransactionResponse response = transactionService.confirmTransactionWithOTP(confirmTransactionRequest);
        return MessageResponse.<TransactionResponse>builder()
                .status((short) HttpStatus.OK.value())
                .message(Constants.DEFAULT_MESSAGE_CREATE_SUCCESS)
                .data(response)
                .localDateTime(String.valueOf(Instant.now()))
                .build();
    }
}

