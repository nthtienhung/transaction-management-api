package com.transactionservice.service.impl;

import com.transactionservice.dto.request.TransactionListRequest;
import com.transactionservice.dto.response.TransactionListResponse;
import com.transactionservice.entity.Transaction;
import com.transactionservice.mapper.TransactionMapper;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.service.TransactionService;
import com.transactionservice.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Page<TransactionListResponse> getTransactions(TransactionListRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Specification<Transaction> spec = Specification.where(TransactionSpecification.hasTransactionCode(request.getTransactionCode()))
                .and(TransactionSpecification.hasSenderWalletId(request.getSenderWalletId()))
                .and(TransactionSpecification.hasRecipientWalletId(request.getRecipientWalletId()))
                .and(TransactionSpecification.hasStatus(request.getStatus()))
                .and(TransactionSpecification.isBetweenDate(request.getFromDate(), request.getToDate()));

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);

        return transactions.map(TransactionMapper::toTransactionResponseDTO);
    }
}
