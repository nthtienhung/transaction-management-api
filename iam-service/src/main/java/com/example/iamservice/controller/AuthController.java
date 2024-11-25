package com.example.iamservice.controller;

import com.example.iamservice.dto.request.login.LoginRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.dto.response.login.TokenResponse;
import com.example.iamservice.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
    private final LoginService loginService;
    @GetMapping("")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("hello", HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                                   @RequestBody LoginRequest loginRequest) {
        return this.loginService.authorize(request, loginRequest);
    }

}