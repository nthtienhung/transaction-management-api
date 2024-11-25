package com.example.iamservice.service;

import com.example.iamservice.dto.request.signup.SignUpRequest;

public interface AuthService {

    void signUp(SignUpRequest request);
}
