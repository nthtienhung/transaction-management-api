package com.example.userservice.controller;

import com.example.userservice.dto.response.ResponseData;
import com.example.userservice.dto.response.common.ResponseObject;
import com.example.userservice.entity.Profile;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request) {
        return this.userService.getProfile(request);
    }
}
