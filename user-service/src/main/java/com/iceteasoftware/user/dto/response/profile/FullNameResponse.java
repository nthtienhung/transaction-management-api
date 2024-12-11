package com.iceteasoftware.user.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FullNameResponse implements Serializable {

    private String firstName;

    private String lastName;
}
