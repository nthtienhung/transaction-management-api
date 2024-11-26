package com.example.iamservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String newPassword;
    private String confirmPassword;
    private String email;
    private String resetPasswordKey;
}
