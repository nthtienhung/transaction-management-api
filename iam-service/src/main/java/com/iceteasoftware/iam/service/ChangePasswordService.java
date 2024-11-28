package com.iceteasoftware.iam.service;

import com.iceteasoftware.iam.dto.request.changepassword.ChangePasswordRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface ChangePasswordService {

    /**
     * Đổi mật kẩu người dùng bằng email
     */
    ResponseEntity<ResponseObject> changePasswordByEmail(ChangePasswordRequest request);
}



