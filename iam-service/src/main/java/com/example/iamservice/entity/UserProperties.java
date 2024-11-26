package com.example.iamservice.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "user")
public class UserProperties {
    private int preRegisterVerifyLimit;

    private int loginAttempts;

    private int loginFailedLockDuration;

    private int resetPwVerifyLimit;
}
