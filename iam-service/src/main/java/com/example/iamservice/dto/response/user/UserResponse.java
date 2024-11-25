package com.example.iamservice.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String userId;
    private String isVerified;
    private String email;
    private String password;
    private boolean status;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
