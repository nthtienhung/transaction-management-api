package com.example.iamservice.client;

import com.example.iamservice.configuration.security.AuthenticationRequestInterceptor;
import com.example.iamservice.dto.request.signup.CreateProfileRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8084/user",
        configuration = { AuthenticationRequestInterceptor.class })
public interface UserClient {

    @PostMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseObject<String> createProfile(@RequestBody CreateProfileRequest request);

    @GetMapping(value = "/check-phone-exists", produces = MediaType.APPLICATION_JSON_VALUE)
    Boolean checkPhoneExists(@RequestParam String phone);
}
