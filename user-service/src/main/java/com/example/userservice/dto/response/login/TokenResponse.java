package com.example.userservice.dto.response.login;

import com.example.userservice.annotation.Exclude;
import com.example.userservice.dto.response.common.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class TokenResponse extends Response {
    private static final long serialVersionUID = 17872987319L;
    @Exclude
    private String csrfToken;
    @Exclude
    private String refreshToken;
    private String type;
    private Integer csrfTokenDuration;
    private Integer refreshTokenDuration;
    private Date csrfExpiresAt;

}
