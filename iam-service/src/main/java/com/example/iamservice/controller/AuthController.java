package com.example.iamservice.controller;

import com.example.iamservice.constant.Constants;
import com.example.iamservice.dto.request.EmailRequest;
import com.example.iamservice.dto.request.OTPRequest;
import com.example.iamservice.dto.request.ResetPasswordRequest;
import com.example.iamservice.dto.request.changepassword.ChangePasswordRequest;
import com.example.iamservice.dto.request.login.LoginRequest;
import com.example.iamservice.dto.response.ForgotPasswordResponse;
import com.example.iamservice.dto.response.OTPResponse;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.dto.response.login.TokenResponse;
import com.example.iamservice.service.ChangePasswordService;
import com.example.iamservice.service.ForgotPasswordService;
import com.example.iamservice.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/forgotPassword")
@RequiredArgsConstructor
public class AuthController {

    private final ForgotPasswordService forgotPasswordService;
    private final LoginService loginService;
    private final ChangePasswordService userService;

    @PostMapping()
    public ResponseObject<ForgotPasswordResponse> verifyMail(@RequestBody EmailRequest email) throws MessagingException, IOException {
        forgotPasswordService.verifyMail(email);
        return new ResponseObject<>(HttpStatus.OK.value(),Constants.DEFAULT_MESSAGE_SUCCESS,
                LocalDateTime.now());
    }

    @PostMapping("/OTP")
    public ResponseObject<OTPResponse> verifyOTP(@RequestBody OTPRequest request) {
        return new ResponseObject<>(Constants.DEFAULT_MESSAGE_SUCCESS_TOTP, HttpStatus.OK.value(),
                LocalDateTime.now(), forgotPasswordService.verifyOTP(request));
    }

    @PostMapping("/generateOTP")
    public ResponseObject<String> generateOTP(@RequestParam String email) {
        forgotPasswordService.generateOtp(email);
        return new ResponseObject<>(HttpStatus.OK.value(),Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }
    @PostMapping("/resetPassword")
    public ResponseObject<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return new ResponseObject<>(Constants.DEFAULT_MESSAGE_UPDATE_SUCCESS, HttpStatus.OK.value(),
                LocalDateTime.now(), forgotPasswordService.resetPassword(request));
    }


    @GetMapping("")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("hello", HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                                   @RequestBody LoginRequest loginRequest) {
        return this.loginService.authorize(request, loginRequest);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePasswordByEmail(request);
    }

}

