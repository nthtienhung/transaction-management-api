package com.transactionservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transactionservice.constant.Constants;
import com.transactionservice.dto.request.TransactionRequest;
import com.transactionservice.dto.response.MessageResponse;
import com.transactionservice.dto.response.TransactionResponse;
import com.transactionservice.entity.Transaction;
import com.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

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
}