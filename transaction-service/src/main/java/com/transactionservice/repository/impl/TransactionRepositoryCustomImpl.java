package com.transactionservice.repository.impl;

import com.transactionservice.entity.Transaction;
import com.transactionservice.repository.TransactionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<Transaction> findFilteredTransactions(
            String walletCodeByUserLogIn,
            String walletCodeByUserSearch,
            String transactionCode,
            String status,
            Instant fromDate,
            Instant toDate,
            Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Truy vấn chính (Main Query)
        CriteriaQuery<Transaction> mainQuery = cb.createQuery(Transaction.class);
        Root<Transaction> mainRoot = mainQuery.from(Transaction.class);

        List<Predicate> mainPredicates = new ArrayList<>();

        // Logic lọc ví (wallet filtering logic)
        if (walletCodeByUserSearch != null) {
            Predicate walletFilter = cb.or(
                    cb.and(cb.equal(mainRoot.get("senderWalletCode"), walletCodeByUserLogIn),
                            cb.equal(mainRoot.get("recipientWalletCode"), walletCodeByUserSearch)),
                    cb.and(cb.equal(mainRoot.get("recipientWalletCode"), walletCodeByUserSearch),
                            cb.equal(mainRoot.get("senderWalletCode"), walletCodeByUserLogIn))
            );
            mainPredicates.add(walletFilter);
        }

        // Lọc theo transactionCode
        if (transactionCode != null) {
            mainPredicates.add(cb.equal(mainRoot.get("transactionCode"), transactionCode));
        }

        // Lọc theo trạng thái (status)
        if (status != null) {
            mainPredicates.add(cb.equal(mainRoot.get("status"), status));
        }

        // Lọc theo khoảng thời gian (fromDate, toDate)
        if (fromDate != null) {
            mainPredicates.add(cb.greaterThanOrEqualTo(mainRoot.get("createdDate"), fromDate));
        }

        if (toDate != null) {
            mainPredicates.add(cb.lessThanOrEqualTo(mainRoot.get("createdDate"), toDate));
        }

        mainQuery.where(cb.and(mainPredicates.toArray(new Predicate[0])));
        mainQuery.orderBy(cb.desc(mainRoot.get("createdDate")));

        // Thực thi câu lệnh chính
        var query = entityManager.createQuery(mainQuery);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Transaction> transactions = query.getResultList();

        // Câu lệnh đếm (Count Query)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Transaction> countRoot = countQuery.from(Transaction.class);

        List<Predicate> countPredicates = new ArrayList<>();

        // Áp dụng logic lọc tương tự cho câu lệnh đếm
        if (walletCodeByUserSearch != null) {
            Predicate walletFilter = cb.or(
                    cb.and(cb.equal(countRoot.get("senderWalletCode"), walletCodeByUserLogIn),
                            cb.equal(countRoot.get("recipientWalletCode"), walletCodeByUserSearch)),
                    cb.and(cb.equal(countRoot.get("recipientWalletCode"), walletCodeByUserSearch),
                            cb.equal(countRoot.get("senderWalletCode"), walletCodeByUserLogIn))
            );
            countPredicates.add(walletFilter);
        }

        if (transactionCode != null) {
            countPredicates.add(cb.equal(countRoot.get("transactionCode"), transactionCode));
        }

        if (status != null) {
            countPredicates.add(cb.equal(countRoot.get("status"), status));
        }

        if (fromDate != null) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdDate"), fromDate));
        }

        if (toDate != null) {
            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdDate"), toDate));
        }

        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        var countResult = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(transactions, pageable, countResult);
    }

}
