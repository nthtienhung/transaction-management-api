package com.example.iamservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class OTPValue {
    private String email;

    private String otp;

    private Date expirationTime;

    private String key;

    private int count;

    private int otpConfirmCount;


    public OTPValue(String otp, Date expirationTime) {
        this.otp = otp;
        this.expirationTime = expirationTime;
        this.count = 1;
        this.otpConfirmCount = 1;
    }
    public OTPValue(String otp, Date expirationTime, int count) {
        this.otp = otp;
        this.expirationTime = expirationTime;
        this.count = count + 1;
        this.otpConfirmCount = 1;
    }

    public OTPValue(String otp, int count, int otpConfirmCount) {
        this.otp = otp;
        this.count = count;
        this.otpConfirmCount = otpConfirmCount + 1;
    }

}

