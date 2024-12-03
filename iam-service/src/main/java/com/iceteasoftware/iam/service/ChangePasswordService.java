package com.iceteasoftware.iam.service;

import com.iceteasoftware.iam.dto.request.changepassword.ChangePasswordRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface ChangePasswordService {

    ResponseEntity<ResponseObject> changePasswordByEmail(ChangePasswordRequest request);
}



