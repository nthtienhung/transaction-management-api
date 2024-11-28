package com.iceteasoftware.iam.dto.request.login;

import com.iceteasoftware.iam.annotation.Exclude;
import com.iceteasoftware.iam.dto.request.common.Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest extends Request {
    private static final long serialVersionUID = -423423423424L;
    private String email;
    @Exclude
    private String password;
    private Boolean rememberMe;

    @Override
    public String toString() {
        return "LoginRequest{" + "email='" + this.email + '\'' + ", rememberMe=" + this.rememberMe + '}';
    }

}
