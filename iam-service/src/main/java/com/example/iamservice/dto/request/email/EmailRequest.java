package com.example.iamservice.dto.request.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {

    private String userId;

    private String topicName;

    private String data;

}
