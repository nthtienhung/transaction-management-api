package com.iceteasoftware.iam.repository;

import com.iceteasoftware.iam.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Integer> {
    List<PasswordHistory> findTop5ByEmailOrderByCreatedAtDesc(String email);
}
