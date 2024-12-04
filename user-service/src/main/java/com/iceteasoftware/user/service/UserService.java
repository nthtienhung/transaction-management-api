package com.iceteasoftware.user.service;

import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.entity.Profile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request);

    void createProfile(CreateProfileRequest request);

    boolean isPhoneExists(String phone);

    String getRole(HttpServletRequest request);

    ResponseEntity<ResponseObject<Profile>> updateProfile(
            HttpServletRequest request,
            CreateProfileRequest updateRequest);
}
