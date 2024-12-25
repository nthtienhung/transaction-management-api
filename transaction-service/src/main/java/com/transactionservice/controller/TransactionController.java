package com.transactionservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.dto.request.ConfirmTransactionRequest;
import com.transactionservice.dto.request.email.EmailRequest;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.enums.Status;

import com.transactionservice.dto.request.TransactionListRequest;
import com.transactionservice.dto.response.*;
import com.transactionservice.dto.request.TransactionSearch;
import com.transactionservice.service.TransactionService;
import com.transactionservice.constant.Constants;
import com.transactionservice.dto.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/recent-received-transaction-list-by-user")
    public MessageResponse<Page<TransactionDashboardResponse>> getRecentReceivedTransactionList(@RequestParam String walletCodeByUserLogIn) {
        Page<TransactionDashboardResponse> data = transactionService.getRecentReceivedTransactionListByUser(walletCodeByUserLogIn);
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @GetMapping("/recent-sent-transaction-list-by-user")
    public MessageResponse<Page<TransactionDashboardResponse>> getRecentSentTransactionList(@RequestParam String walletCodeByUserLogIn) {
        Page<TransactionDashboardResponse> data = transactionService.getRecentSentTransactionListByUser(walletCodeByUserLogIn);
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @GetMapping("/total-amount-sent-transaction-by-user-in-week")
    public MessageResponse<Double> getTotalAmountSentTransactionByUser(@RequestParam String senderWalletCode) {
        Double data = transactionService.getTotalSentTransactionByUserInWeek(senderWalletCode);
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @GetMapping("/total-amount-received-transaction-by-user-in-week")
    public MessageResponse<Double> getTotalAmountReceivedTransactionByUser(@RequestParam String recipientWalletCode) {
        Double data = transactionService.getTotalReceivedTransactionByUserInWeek(recipientWalletCode);
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @GetMapping("/total-transaction-by-user")
    public MessageResponse<Integer> getTotalTransactionByUser(@RequestParam String walletCode) {
        Integer data = transactionService.getTotalTransactionByUser(walletCode);
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @PostMapping("/create-transaction")
    public MessageResponse<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) throws JsonProcessingException {
        TransactionResponse savedTransaction = transactionService.createTransaction(transactionRequest);
        return MessageResponse.<TransactionResponse>builder().status((short) HttpStatus.CREATED.value()).message(Constants.DEFAULT_MESSAGE_SUCCESS).data(savedTransaction).localDateTime(String.valueOf(Instant.now())).build();
    }

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

    /**
     * API xác nhận giao dịch với OTP
     */
    @PostMapping("/confirm-transaction")
    public MessageResponse<TransactionResponse> initiateTransaction(@RequestBody ConfirmTransactionRequest confirmTransactionRequest) throws JsonProcessingException {
        TransactionResponse response = transactionService.initiateTransaction(confirmTransactionRequest);
        return MessageResponse.<TransactionResponse>builder()
                .status((short) HttpStatus.OK.value())
                .message(Constants.DEFAULT_MESSAGE_CREATE_SUCCESS)
                .data(response)
                .localDateTime(String.valueOf(Instant.now()))
                .build();
    }

    @GetMapping("/transaction-list-by-user")
    public MessageResponse<Page<TransactionListResponse>> getTransactionListByUser(@ModelAttribute TransactionListRequest request) {
        Page<TransactionListResponse> data = transactionService.getTransactionListByUser(request);
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @PostMapping("/getAllTransaction")
    public ResponseEntity<Page<TransactionSearchResponse>> getAllTransaction(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String walletCode,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam int page,
            @RequestParam int size) {
        Instant fromInstant = fromDate != null ? Instant.parse(fromDate) : null;
        Instant toInstant = toDate != null ? Instant.parse(toDate) : null;
        TransactionSearch transactionSearch = new TransactionSearch(transactionId, walletCode, status, fromInstant, toInstant);
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable từ tham số
        Page<TransactionSearchResponse> transactionSearchResponses =
                transactionService.getTransactionByInformation(transactionSearch, pageable);
        return new ResponseEntity<>(transactionSearchResponses, HttpStatus.OK);
    }

}

