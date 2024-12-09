package com.transactionservice.service;

import com.transactionservice.dto.response.transaction.TransactionResponse;

import java.util.List;

/**
 * Author: thinhtd
 * Date: 12/9/2024
 * Time: 3:27 PM
 */
public interface TransactionService {

    List<TransactionResponse> getRecentReceivedTransactionListByUser();

    List<TransactionResponse> getRecentSentTransactionListByUser();

}
