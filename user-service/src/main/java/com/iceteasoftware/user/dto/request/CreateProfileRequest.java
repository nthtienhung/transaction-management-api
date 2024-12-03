package com.iceteasoftware.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateProfileRequest {

    private String firstName;

    private String lastName;

    private LocalDate dob;

    private String phone;

    private String address;

    private String userId;

    private String email;

}
