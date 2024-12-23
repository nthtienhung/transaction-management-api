package com.transactionservice.client;


import com.transactionservice.configuration.security.AuthenticationRequestInterceptor;
import com.transactionservice.dto.response.FullNameResponse;
import com.transactionservice.dto.response.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${feign.client.config.user-service.name}",
        url = "${feign.client.config.user-service.url}",
        configuration = AuthenticationRequestInterceptor.class)
public interface UserClient {
    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId);

    @GetMapping("/check-email-exists")
    Boolean isEmailExists(@RequestParam String email);

    @GetMapping("/{userId}/full-name")
    FullNameResponse getFullNameByUserId(@PathVariable("userId") String userId);

    @GetMapping("/user-id")
    String getUserIdByUsername(@RequestParam String username);

}