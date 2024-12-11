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
import java.util.Optional;
import java.util.function.BiFunction;

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

        // Logic lọc chung
        BiFunction<CriteriaBuilder, Root<Transaction>, Predicate> buildFilters = (builder, root) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (walletCodeByUserSearch != null) {
                predicates.add(cb.or(
                        cb.and(
                                cb.equal(root.get("senderWalletCode"), walletCodeByUserLogIn),
                                cb.equal(root.get("recipientWalletCode"), walletCodeByUserSearch)
                        ),
                        cb.and(
                                cb.equal(root.get("recipientWalletCode"), walletCodeByUserLogIn),
                                cb.equal(root.get("senderWalletCode"), walletCodeByUserSearch)
                        )
                ));
            } else {
                predicates.add(cb.or(
                        cb.equal(root.get("senderWalletCode"), walletCodeByUserLogIn),
                        cb.equal(root.get("recipientWalletCode"), walletCodeByUserLogIn)
                ));
            }

            Optional.ofNullable(transactionCode).ifPresent(code ->
                    predicates.add(cb.equal(root.get("transactionCode"), code))
            );

            Optional.ofNullable(status).ifPresent(st ->
                    predicates.add(cb.equal(root.get("status"), st))
            );

            Optional.ofNullable(fromDate).ifPresent(from ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), from))
            );

            Optional.ofNullable(toDate).ifPresent(to ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), to))
            );

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        // Main Query
        CriteriaQuery<Transaction> mainQuery = cb.createQuery(Transaction.class);
        Root<Transaction> mainRoot = mainQuery.from(Transaction.class);
        mainQuery.where(buildFilters.apply(cb, mainRoot));
        mainQuery.orderBy(cb.desc(mainRoot.get("createdDate")));

        var query = entityManager.createQuery(mainQuery);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Transaction> transactions = query.getResultList();

        // Count Query
        long countResult = pageable.isUnpaged() ? transactions.size() : calculateTotalCount(cb, buildFilters, walletCodeByUserLogIn, walletCodeByUserSearch);

        return new PageImpl<>(transactions, pageable, countResult);
    }
    private long calculateTotalCount(CriteriaBuilder cb,
                                     BiFunction<CriteriaBuilder, Root<Transaction>, Predicate> buildFilters,
                                     String walletCodeByUserLogIn,
                                     String walletCodeByUserSearch) {

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Transaction> countRoot = countQuery.from(Transaction.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(buildFilters.apply(cb, countRoot));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
