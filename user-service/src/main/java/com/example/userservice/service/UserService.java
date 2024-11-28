package com.example.userservice.service;

import com.example.userservice.dto.request.CreateProfileRequest;
import com.example.userservice.dto.response.common.ResponseObject;
import com.example.userservice.entity.Profile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request);

    void createProfile(CreateProfileRequest request);

    boolean isPhoneExists(String phone);
}
