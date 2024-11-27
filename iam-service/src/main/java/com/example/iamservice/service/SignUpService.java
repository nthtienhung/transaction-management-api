package com.example.iamservice.service;

import com.example.iamservice.dto.request.signup.SignUpRequest;
import com.example.iamservice.dto.request.signup.VerifyUserRequest;

public interface SignUpService {

    void signUp(SignUpRequest request);

    void generateOtp(String email);

    void verifyUser(VerifyUserRequest request);

}
