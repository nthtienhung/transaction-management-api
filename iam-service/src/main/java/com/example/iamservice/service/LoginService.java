package com.example.iamservice.service;

import com.example.iamservice.dto.request.login.LoginRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.dto.response.login.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface LoginService {

    ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                            LoginRequest loginRequest);
}


