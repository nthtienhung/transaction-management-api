package com.transactionservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OTPRequest {
    private String email;
    private String otp;
    private String amount;
}
