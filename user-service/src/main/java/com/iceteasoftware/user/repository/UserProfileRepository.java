package com.iceteasoftware.user.repository;

import com.iceteasoftware.user.dto.response.profile.FullNameResponse;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable; 

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByPhone(String phone);

    Optional<Profile> findByUserId(String userId);

    Page<Profile> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, 
        String lastName, 
        Pageable pageable);


    @Query(value = "SELECT new com.iceteasoftware.user.dto.response.profile.FullNameResponse(u.firstName, u.lastName) " +
            "FROM Profile u WHERE u.userId = :userId")
    Optional<FullNameResponse> findUserById(String userId);

    @Query(value = "SELECT u.userId FROM Profile u WHERE u.email = :email")
    String findUserIdByUsername(String username);
}
