package com.example.iamservice.service;

import com.example.iamservice.dto.request.changepassword.ChangePasswordRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface ChangePasswordService {

    /**
     * Đổi mật kẩu người dùng bằng email
     */
    ResponseEntity<ResponseObject> changePasswordByEmail(ChangePasswordRequest request);
}



