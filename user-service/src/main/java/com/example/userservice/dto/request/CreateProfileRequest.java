package com.example.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateProfileRequest {

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String phone;

    private String address;

    private String userId;

}
