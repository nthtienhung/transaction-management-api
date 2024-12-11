package com.iceteasoftware.iam.service;

import com.iceteasoftware.iam.dto.request.login.LoginRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import com.iceteasoftware.iam.dto.response.login.TokenResponse;
import com.iceteasoftware.iam.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface LoginService {

    ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                            LoginRequest loginRequest);
    Optional<User> getUser(String email);

    ResponseEntity<ResponseObject<TokenResponse>> refreshToken(HttpServletRequest request);
}


