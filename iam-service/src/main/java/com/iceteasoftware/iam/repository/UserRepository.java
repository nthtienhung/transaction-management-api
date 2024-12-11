package com.iceteasoftware.iam.repository;

import com.iceteasoftware.iam.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.iam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("select new com.iceteasoftware.iam.dto.response.StatusRoleUserResponse(u.role, u.status) from User u " +
            "where u.userId = :userId")
    Optional<StatusRoleUserResponse> findStatusRoleUser(String userId);

}
