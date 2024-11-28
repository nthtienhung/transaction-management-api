package com.iceteasoftware.iam.service;

import com.iceteasoftware.iam.dto.request.signup.SignUpRequest;
import com.iceteasoftware.iam.dto.request.signup.VerifyUserRequest;

public interface SignUpService {

    void signUp(SignUpRequest request);

    void generateOtp(String email);

    void verifyUser(VerifyUserRequest request);

}
