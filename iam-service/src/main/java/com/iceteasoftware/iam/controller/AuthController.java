package com.iceteasoftware.iam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceteasoftware.iam.constant.Constants;
import com.iceteasoftware.iam.dto.request.changepassword.ChangePasswordRequest;
import com.iceteasoftware.iam.dto.request.login.LoginRequest;
import com.iceteasoftware.iam.dto.request.signup.SignUpRequest;
import com.iceteasoftware.iam.dto.request.signup.VerifyUserRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import com.iceteasoftware.iam.dto.response.login.TokenResponse;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.service.*;
import com.iceteasoftware.iam.dto.request.EmailRequest;
import com.iceteasoftware.iam.dto.request.OTPRequest;
import com.iceteasoftware.iam.dto.request.ResetPasswordRequest;
import com.iceteasoftware.iam.dto.response.ForgotPasswordResponse;
import com.iceteasoftware.iam.dto.response.OTPResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final SignUpService signUpService;
    private final ForgotPasswordService forgotPasswordService;
    private final LoginService loginService;
    private final ChangePasswordService changePasswordService;
    private final LogoutService logoutService;

    @PostMapping("/forgot-password/verify-mail")
    public ResponseObject<ForgotPasswordResponse> verifyMail(@RequestBody EmailRequest email) throws MessagingException, IOException {
        forgotPasswordService.verifyMail(email);
        return new ResponseObject<>(HttpStatus.OK.value(),Constants.DEFAULT_MESSAGE_SUCCESS,
                LocalDateTime.now());
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseObject<OTPResponse> verifyOTP(@RequestBody OTPRequest request) {
        return new ResponseObject<>(Constants.DEFAULT_MESSAGE_SUCCESS_TOTP, HttpStatus.OK.value(),
                LocalDateTime.now(), forgotPasswordService.verifyOTP(request));
    }

    @PostMapping("/forgot-password/generate")
    public ResponseObject<String> generateOTP(@RequestBody EmailRequest request) throws JsonProcessingException {
        forgotPasswordService.generateOtp(request);
        return new ResponseObject<>(HttpStatus.OK.value(),Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @PostMapping("/forgot-password/reset")
    public ResponseObject<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        forgotPasswordService.resetPassword(request);
        return new ResponseObject<>(HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_UPDATE_SUCCESS,
                LocalDateTime.now());
    }

    @GetMapping("/test")
    public String login() {
        return "hello";
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                                   @RequestBody LoginRequest loginRequest) {
        return this.loginService.authorize(request, loginRequest);
    }
    @GetMapping("/logoutAccount")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logoutService.logout(request);
        return new ResponseEntity<>(MessageCode.MSG1041,HttpStatus.OK);
    }
    /**
     * API endpoint to handle password change requests.
     *
     * @param request the {@link ChangePasswordRequest} containing the user's email, old password,
     *                new password, and confirmation password.
     * @return a {@link ResponseEntity} containing a success message if the password is changed successfully,
     *         or an error message if validation fails.
     */
    @PostMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordRequest request) {
        return changePasswordService.changePasswordByEmail(request);
    }

    /**
     * Handles user registration by accepting a sign-up request.
     *
     * @param request the {@link SignUpRequest} object containing user information for sign-up.
     * @return a {@link ResponseObject} containing a success message if the registration is successful.
     */
    @PostMapping("/register")
    public ResponseObject<String> signUp(@Valid @RequestBody SignUpRequest request) throws JsonProcessingException {
        signUpService.signUp(request);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    /**
     * Generates a One-Time Password (OTP) for the provided email and sends it via Kafka for email delivery.
     *
     * @param request the {@link EmailRequest} object containing the email address for which the OTP is generated.
     * @return a {@link ResponseObject} containing a success message if the OTP is generated successfully.
     */
    @PostMapping("/register/generate-otp")
    public ResponseObject<String> generateOtp(@RequestBody EmailRequest request) throws JsonProcessingException {
        log.info("Generating OTP for email: {}", request);
        signUpService.generateOtp(request);
        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    /**
     * Verifies a user's OTP for account activation.
     *
     * @param request the {@link VerifyUserRequest} object containing the user's email and OTP for verification.
     * @return a {@link ResponseObject} containing a success message if the OTP is verified successfully.
     */
    @PostMapping("/register/verify")
    public ResponseObject<String> verifyOtp(@RequestBody VerifyUserRequest request){
        signUpService.verifyUser(request);
        return new ResponseObject<>(HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }
}

