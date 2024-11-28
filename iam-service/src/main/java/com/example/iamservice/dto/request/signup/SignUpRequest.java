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

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String address;
    private String phone;

}
