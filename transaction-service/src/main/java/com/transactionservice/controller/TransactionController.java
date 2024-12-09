package com.transactionservice.controller;

import com.transactionservice.dto.response.common.ResponseObject;
import com.transactionservice.dto.response.transaction.TransactionResponse;
import com.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Author: thinhtd
 * Date: 12/9/2024
 * Time: 3:14 PM
 */

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/recent-received-transaction-list-by-user")
    public ResponseObject<List<TransactionResponse>> getRecentReceivedTransactionList() {
        List<TransactionResponse> data = transactionService.getRecentReceivedTransactionListByUser();
        return new ResponseObject<>(HttpStatus.OK.value(), "Success", LocalDateTime.now(), data);
    }

    @GetMapping("/recent-sent-transaction-list-by-user")
    public ResponseObject<List<TransactionResponse>> getRecentSentTransactionList() {
        List<TransactionResponse> data = transactionService.getRecentSentTransactionListByUser();
        return new ResponseObject<>(HttpStatus.OK.value(), "Success", LocalDateTime.now(), data);
    }

}
