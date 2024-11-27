package com.example.iamservice.dto.request.signup;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class VerifyUserRequest implements Serializable {

    String email;

    String otp;

}
