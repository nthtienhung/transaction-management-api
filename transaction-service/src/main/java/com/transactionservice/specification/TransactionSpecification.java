package com.transactionservice.specification;

import com.transactionservice.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {
    public static Specification<Transaction> hasTransactionCode(String transactionCode) {
        return (root, query, criteriaBuilder) ->
            transactionCode == null ? null : criteriaBuilder.equal(root.get("transactionCode"), transactionCode);
    }

    public static Specification<Transaction> hasSenderWalletId(String senderWalletId) {
        return (root, query, criteriaBuilder) ->
            senderWalletId == null ? null : criteriaBuilder.equal(root.get("senderWalletId"), senderWalletId);
    }

    public static Specification<Transaction> hasRecipientWalletId(String recipientWalletId) {
        return (root, query, criteriaBuilder) ->
            recipientWalletId == null ? null : criteriaBuilder.equal(root.get("recipientWalletId"), recipientWalletId);
    }

    public static Specification<Transaction> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
            status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Transaction> isBetweenDate(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate != null && toDate != null) {
                return criteriaBuilder.between(root.get("createdAt"), fromDate, toDate);
            } else if (fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
            } else if (toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate);
            }
            return null;
        };
    }
}
