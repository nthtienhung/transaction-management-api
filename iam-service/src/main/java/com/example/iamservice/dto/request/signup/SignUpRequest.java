package com.example.iamservice.dto.request.signup;

import com.example.iamservice.constant.Constants;
import com.example.iamservice.constant.RegularExpressionConstants;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class SignUpRequest implements Serializable {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters long and most 20 characters")
    @Pattern(
            regexp = RegularExpressionConstants.DEFAULT_REGEXP_PASSWORD,
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    private Date dateOfBirth;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = RegularExpressionConstants.DEFAULT_REGEXP_PHONE_NUMBER,
            message = "Invalid phone"
    )
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;



}
