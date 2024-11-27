package com.example.iamservice.controller;

import com.example.iamservice.constant.Constants;
import com.example.iamservice.dto.request.changepassword.ChangePasswordRequest;
import com.example.iamservice.dto.request.login.LoginRequest;
import com.example.iamservice.dto.request.signup.SignUpRequest;
import com.example.iamservice.dto.request.signup.VerifyUserRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.dto.response.login.TokenResponse;
import com.example.iamservice.service.ChangePasswordService;
import com.example.iamservice.service.LoginService;
import com.example.iamservice.service.SignUpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SignUpService signUpService;
    private final LoginService loginService;
    private final ChangePasswordService changePasswordService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                                   @RequestBody LoginRequest loginRequest) {
        return this.loginService.authorize(request, loginRequest);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordRequest request) {
        return changePasswordService.changePasswordByEmail(request);
    }

    @PostMapping("/register")
    public ResponseObject<String> signUp(@Valid @RequestBody SignUpRequest request) {
        signUpService.signUp(request);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @PostMapping("/register/generate-otp")
    public ResponseObject<String> generateOtp(@RequestParam String email){
        signUpService.generateOtp(email);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @PostMapping("/register/verify")
    public ResponseObject<String> verifyOtp(@RequestBody VerifyUserRequest request){
        signUpService.verifyUser(request);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

}

