package com.example.iamservice.controller;

import com.example.iamservice.constant.Constants;
import com.example.iamservice.dto.request.EmailRequest;
import com.example.iamservice.dto.request.OTPRequest;
import com.example.iamservice.dto.request.ResetPasswordRequest;
import com.example.iamservice.dto.response.ForgotPasswordResponse;
import com.example.iamservice.dto.response.OTPResponse;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping()
    public ResponseObject<ForgotPasswordResponse> verifyMail(@RequestBody EmailRequest email) throws MessagingException, IOException {
        forgotPasswordService.verifyMail(email);
        return new ResponseObject<>(Constants.DEFAULT_MESSAGE_SUCCESS, HttpStatus.OK.value(),
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
        return new ResponseObject<>(Constants.DEFAULT_MESSAGE_SUCCESS, HttpStatus.OK.value(), LocalDateTime.now());
    }
    @PostMapping("/resetPassword")
    public ResponseObject<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return new ResponseObject<>(Constants.DEFAULT_MESSAGE_UPDATE_SUCCESS, HttpStatus.OK.value(),
                LocalDateTime.now(), forgotPasswordService.resetPassword(request));
    }

}
