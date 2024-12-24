package com.iceteasoftware.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusRoleUserResponse {

    private String role;
    private String isVerified;
    private Boolean status;

}
