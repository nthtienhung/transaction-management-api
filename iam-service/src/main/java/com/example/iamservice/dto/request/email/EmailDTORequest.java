package com.example.iamservice.dto.request.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDTORequest {

    private String userId;

    private String topicName;

    private String data;

}
