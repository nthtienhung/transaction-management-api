package com.example.iamservice.configuration.security.jwt;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class JWTAccessToken implements Serializable{

    @Serial
    private static final long serialVersionUID = 2271652818578387603L;

    private JWTToken accessToken;

    private JWTToken csrfToken;

}

