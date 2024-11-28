package com.example.userservice.controller;

import com.example.userservice.constant.Constants;
import com.example.userservice.dto.request.CreateProfileRequest;
import com.example.userservice.dto.response.common.ResponseObject;
import com.example.userservice.entity.Profile;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request) {
        return this.userService.getProfile(request);
    }

    @PostMapping("/profile")
    public com.example.userservice.dto.response.ResponseObject<String> createProfile(@RequestBody CreateProfileRequest request){
        userService.createProfile(request);
        return new com.example.userservice.dto.response.ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @GetMapping("/check-phone-exists")
    public Boolean isPhoneExists(@RequestParam String phone){
        return userService.isPhoneExists(phone);
    }
}
