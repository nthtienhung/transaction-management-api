package com.example.iamservice.configuration.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTToken implements Serializable{

    @Serial
    private static final long serialVersionUID = 6079085501370429127L;

    private String token;

    private int duration;

    private Date expiredTime;

    public JWTToken(String token) {
        this.token = token;
    }
}

