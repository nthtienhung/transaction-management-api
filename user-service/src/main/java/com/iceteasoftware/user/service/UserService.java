package com.iceteasoftware.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceteasoftware.user.dto.UserProfileResponse;
import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.UserResponse;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.dto.response.profile.FullNameResponse;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.entity.User;
import com.iceteasoftware.user.enums.Status;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

// import java.awt.print.Pageable;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request);

    void createProfile(String createProfileMessage) throws JsonProcessingException;

    boolean isPhoneExists(String phone);

    String getRole(HttpServletRequest request);

    ResponseEntity<ResponseObject<Profile>> updateProfile(
            HttpServletRequest request,
            CreateProfileRequest updateRequest);

    ResponseEntity<User> findUser(HttpServletRequest request);

    // List<UserProfileResponse> getAllUserProfile();
    Page<UserProfileResponse> getAllUserProfile(Pageable pageable, String searchTerm);

    UserResponse getUserById(String userId);

    Boolean isEmailExists(String email);

    FullNameResponse getFullNameByUserId(String userId);

    String getUserIdByUsername(String username);

    // void updateUserStatus(String userId, Status status);

}
