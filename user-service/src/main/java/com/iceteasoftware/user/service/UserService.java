package com.iceteasoftware.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.UserResponse;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

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

    List<Profile> getAllProfiles();

    UserResponse getUserById(String userId);
}
