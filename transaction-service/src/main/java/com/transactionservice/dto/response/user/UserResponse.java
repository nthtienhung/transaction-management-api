package com.transactionservice.dto.response.user;

import lombok.Data;

@Data
public class UserResponse {

    private String firstName;

    private String lastName;

    private String email;

    private String address;
}
