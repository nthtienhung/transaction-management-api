package com.example.iamservice.service;

import com.example.iamservice.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponse> searchUserList(String keyword, String status, Pageable pageable);
}

