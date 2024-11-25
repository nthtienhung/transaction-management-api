package com.example.iamservice.dto.request.login;


import com.example.iamservice.dto.request.common.Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenRequest extends Request {
    private String refreshToken;
    private static final long serialVersionUID= 24131231313L;
}
