package com.iceteasoftware.iam.dto.request.signup;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class CreateProfileRequest {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String phone;

    private String address;

    private String userId;

    private String email;

}
