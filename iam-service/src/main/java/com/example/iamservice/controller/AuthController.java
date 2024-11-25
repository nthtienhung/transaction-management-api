package com.example.iamservice.controller;

import com.example.iamservice.constant.Constants;
import com.example.iamservice.dto.request.signup.SignUpRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseObject<String> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }
}
