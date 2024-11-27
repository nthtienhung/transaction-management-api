package com.example.userservice.service;

import com.example.userservice.dto.request.CreateProfileRequest;

public interface UserProfileService {

    void createProfile(CreateProfileRequest request);

    boolean isPhoneExists(String phone);

}
