package com.transactionservice.controler;

import com.transactionservice.entity.Transaction;
import com.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam(value = "transactionCode", required = false) String transactionCode,
            @RequestParam(value = "senderWalletId", required = false) String senderWalletId,
            @RequestParam(value = "recipientWalletId", required = false) String recipientWalletId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Pageable pageable) {
        Page<Transaction> transactions = transactionService.getTransactions(transactionCode, senderWalletId, recipientWalletId, status, fromDate, toDate, pageable);
        return ResponseEntity.ok(transactions);
    }
}
