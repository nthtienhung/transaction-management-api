package com.example.userservice.service.impl;

import com.example.userservice.dto.request.CreateProfileRequest;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.repository.UserProfileRepository;
import com.example.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;


    @Override
    @Transactional
    public void createProfile(CreateProfileRequest request) {
        userProfileRepository.save(UserProfile.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .userId(request.getUserId())
                .build());
    }

    @Override
    public boolean isPhoneExists(String phone) {
        return userProfileRepository.findByPhone(phone).isPresent();
    }
}
