package com.example.iamservice.dto.request.changepassword;

import com.example.iamservice.dto.request.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest extends Request {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
