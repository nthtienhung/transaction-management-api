package com.transactionservice.controller;

import com.transactionservice.dto.request.TransactionSearch;
import com.transactionservice.dto.response.TransactionSearchResponse;
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
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {


    private final TransactionService transactionService;

    @GetMapping("/recent-received-transaction-list-by-user")
    public MessageResponse<List<TransactionResponse>> getRecentReceivedTransactionList() {
        List<TransactionResponse> data = transactionService.getRecentReceivedTransactionListByUser();
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @GetMapping("/recent-sent-transaction-list-by-user")
    public MessageResponse<List<TransactionResponse>> getRecentSentTransactionList() {
        List<TransactionResponse> data = transactionService.getRecentSentTransactionListByUser();
        return new MessageResponse<>((short) HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now(), data);
    }

    @PostMapping("/create-transaction")
    public MessageResponse<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) throws JsonProcessingException {
        TransactionResponse savedTransaction = transactionService.createTransaction(transactionRequest);
        return MessageResponse.<TransactionResponse>builder()
                .status((short) HttpStatus.CREATED.value())
                .message(Constants.DEFAULT_MESSAGE_SUCCESS)
                .data(savedTransaction)
                .localDateTime(String.valueOf(Instant.now()))
                .build();
    }
    @PostMapping("/getAllTransaction")
    public ResponseEntity<List<TransactionSearchResponse>> getAllTransaction(@RequestBody TransactionSearch transactionSearch) {
        List<TransactionSearchResponse> transactionSearchResponses = transactionService.getTransactionByInformation(transactionSearch);
        Collections.reverse(transactionSearchResponses);
        return new ResponseEntity<>(transactionSearchResponses, HttpStatus.OK);
    }
}

