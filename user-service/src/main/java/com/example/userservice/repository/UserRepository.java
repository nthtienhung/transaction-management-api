package com.example.userservice.repository;

import com.example.userservice.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

}
