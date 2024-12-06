package com.iceteasoftware.iam.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;


public interface LogoutService {
    public void logout(HttpServletRequest request);
}
