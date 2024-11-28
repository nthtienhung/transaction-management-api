package com.example.iamservice.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponse {
    private String otp;
    private String email;
}
