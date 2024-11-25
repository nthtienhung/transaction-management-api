package com.example.iamservice.repository;

import com.example.iamservice.configuration.cache.BusinessCacheConstants;
import com.example.iamservice.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query(
            "SELECT u FROM User u " +
                    "WHERE (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
                    "      (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "    AND (:status IS NULL OR u.status = :status) " +
                    "ORDER BY (CASE u.status " +
                    "WHEN 'Active' THEN 1 " +
                    "WHEN 'Inactive' THEN 2 " +
                    "WHEN 'Block' THEN 3 " +
                    "END)"
    )
    Page<User> getSearchList(String keyword, String status, Pageable pageable);

}