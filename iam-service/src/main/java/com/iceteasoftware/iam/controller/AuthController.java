package com.iceteasoftware.iam.controller;

import com.iceteasoftware.iam.constant.Constants;
import com.iceteasoftware.iam.dto.request.changepassword.ChangePasswordRequest;
import com.iceteasoftware.iam.dto.request.login.LoginRequest;
import com.iceteasoftware.iam.dto.request.signup.SignUpRequest;
import com.iceteasoftware.iam.dto.request.signup.VerifyUserRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import com.iceteasoftware.iam.dto.response.login.TokenResponse;
import com.iceteasoftware.iam.service.ChangePasswordService;
import com.iceteasoftware.iam.dto.request.EmailRequest;
import com.iceteasoftware.iam.dto.request.OTPRequest;
import com.iceteasoftware.iam.dto.request.ResetPasswordRequest;
import com.iceteasoftware.iam.dto.response.ForgotPasswordResponse;
import com.iceteasoftware.iam.dto.response.OTPResponse;
import com.iceteasoftware.iam.service.ForgotPasswordService;
import com.iceteasoftware.iam.service.LoginService;
import com.iceteasoftware.iam.service.SignUpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final SignUpService signUpService;
    private final ForgotPasswordService forgotPasswordService;
    private final LoginService loginService;
    private final ChangePasswordService changePasswordService;

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
    public ResponseObject<String> generateOTP(@RequestParam String email) {
        forgotPasswordService.generateOtp(email);
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
        return new ResponseObject<>(HttpStatus.OK.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }
}

