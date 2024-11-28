package com.iceteasoftware.iam.service;

import com.iceteasoftware.iam.dto.request.EmailRequest;
import com.iceteasoftware.iam.dto.request.OTPRequest;
import com.iceteasoftware.iam.dto.request.ResetPasswordRequest;
import com.iceteasoftware.iam.dto.response.OTPResponse;
import org.springframework.messaging.MessagingException;

import java.io.IOException;

public interface ForgotPasswordService {
    void verifyMail(EmailRequest request) throws MessagingException, IOException;
    OTPResponse verifyOTP(OTPRequest request);
    String resetPassword(ResetPasswordRequest request);

    void generateOtp(String email);
}
