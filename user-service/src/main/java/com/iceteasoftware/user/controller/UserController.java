package com.iceteasoftware.user.controller;

import com.iceteasoftware.user.constant.Constants;
import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.service.UserService;
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
    @GetMapping("/getRole")
    public String getRole(HttpServletRequest request) {
        return this.userService.getRole(request);
    }
    @PostMapping("/profile")
    public com.iceteasoftware.user.dto.response.ResponseObject<String> createProfile(@RequestBody CreateProfileRequest request){
        userService.createProfile(request);
        return new com.iceteasoftware.user.dto.response.ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @GetMapping("/check-phone-exists")
    public Boolean isPhoneExists(@RequestParam String phone){
        return userService.isPhoneExists(phone);
    }
}
