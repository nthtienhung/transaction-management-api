package com.iceteasoftware.iam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceteasoftware.iam.dto.request.EmailRequest;
import com.iceteasoftware.iam.dto.request.signup.SignUpRequest;
import com.iceteasoftware.iam.dto.request.signup.VerifyUserRequest;

public interface SignUpService {

    void signUp(SignUpRequest request) throws JsonProcessingException;

    void generateOtp(EmailRequest request);

    void verifyUser(VerifyUserRequest request);

}
