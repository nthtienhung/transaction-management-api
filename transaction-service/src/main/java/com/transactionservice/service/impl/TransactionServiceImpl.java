package com.transactionservice.service.impl;

import com.transactionservice.dto.response.transaction.TransactionResponse;
import com.transactionservice.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: thinhtd
 * Date: 12/9/2024
 * Time: 3:27 PM
 */

@Service
public class TransactionServiceImpl implements TransactionService {
    @Override
    public List<TransactionResponse> getRecentReceivedTransactionListByUser() {
        return List.of();
    }

    @Override
    public List<TransactionResponse> getRecentSentTransactionListByUser() {
        return List.of();
    }
}
