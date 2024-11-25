package com.example.iamservice.controller;

import com.example.iamservice.constant.Constants;
import com.example.iamservice.dto.request.changepassword.ChangePasswordRequest;
import com.example.iamservice.dto.request.login.LoginRequest;
import com.example.iamservice.dto.request.signup.SignUpRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.dto.response.login.TokenResponse;
import com.example.iamservice.service.AuthService;
import com.example.iamservice.service.ChangePasswordService;
import com.example.iamservice.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginService loginService;
    private final ChangePasswordService changePasswordService;

    @PostMapping("/register")
    public ResponseObject<String> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                                   @RequestBody LoginRequest loginRequest) {
        return this.loginService.authorize(request, loginRequest);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordRequest request) {
        return changePasswordService.changePasswordByEmail(request);
    }
}

