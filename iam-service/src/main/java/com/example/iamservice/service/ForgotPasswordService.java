package com.example.iamservice.service;

import com.example.iamservice.dto.request.EmailRequest;
import com.example.iamservice.dto.request.OTPRequest;
import com.example.iamservice.dto.request.ResetPasswordRequest;
import com.example.iamservice.dto.response.OTPResponse;
import com.example.iamservice.model.OTPValue;
import org.springframework.messaging.MessagingException;

import java.io.IOException;

public interface ForgotPasswordService {
    void verifyMail(EmailRequest request) throws MessagingException, IOException;
    OTPResponse verifyOTP(OTPRequest request);
    String resetPassword(ResetPasswordRequest request);

    void generateOtp(String email);
}
