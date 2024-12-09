package com.transactionservice.controler;

import com.transactionservice.dto.response.TransactionListResponse;
import com.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
public class AuthController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionListResponse>> getTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size, direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending());

        Page<TransactionListResponse> response = transactionService.getTransactions(status, walletId, pageable);
        return ResponseEntity.ok(response);
    }
}
