package com.example.iamservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OTPRequest {
    private String email;
    private String otp;
}
