package com.example.userservice.controller;

import com.example.userservice.constant.Constants;
import com.example.userservice.dto.request.CreateProfileRequest;
import com.example.userservice.dto.response.ResponseObject;
import com.example.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/profile")
    public ResponseObject<String> createProfile(@RequestBody CreateProfileRequest request){
        userProfileService.createProfile(request);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @GetMapping("/check-phone-exists")
    public Boolean isPhoneExists(@RequestParam String phone){
        return userProfileService.isPhoneExists(phone);
    }

}
