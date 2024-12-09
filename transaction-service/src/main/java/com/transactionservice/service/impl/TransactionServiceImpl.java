package com.transactionservice.service.impl;

import com.transactionservice.dto.request.TransactionListRequest;
import com.transactionservice.dto.response.TransactionListResponse;
import com.transactionservice.entity.Transaction;
import com.transactionservice.repository.TransactionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionRepositoryCustom {

    private final EntityManager entityManager;

    public TransactionServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<TransactionListResponse> findFilteredTransactions(TransactionListRequest filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);

        List<Predicate> predicates = new java.util.ArrayList<>();

        // Filter logic for wallet
        Predicate walletPredicate = cb.or(
                cb.equal(root.get("senderWalletCode"), filterRequest.getWalletCode()),
                cb.equal(root.get("receiverWalletCode"), filterRequest.getWalletCode())
        );
        predicates.add(walletPredicate);

        // Additional filters
        if (filterRequest.getTransactionCode() != null && !filterRequest.getTransactionCode().isEmpty()) {
            predicates.add(cb.equal(root.get("transactionCode"), filterRequest.getTransactionCode()));
        }

        if (filterRequest.getStatus() != null && !filterRequest.getStatus().isEmpty()) {
            predicates.add(cb.equal(root.get("status"), filterRequest.getStatus()));
        }

        if (filterRequest.getFromDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filterRequest.getFromDate()));
        }

        if (filterRequest.getToDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filterRequest.getToDate()));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("createdAt")));

        List<Transaction> transactions = entityManager.createQuery(cq).getResultList();

        return transactions.stream()
                .map(t -> new TransactionListResponse(
                        t.getTransactionCode(),
                        t.getSenderWalletCode(),
                        t.getReceiverWalletCode(),
                        t.getAmount(),
                        t.getStatus(),
                        t.getDescription(),
                        t.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
