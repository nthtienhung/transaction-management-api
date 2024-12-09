package com.transactionservice.client;

import com.transactionservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${feign.client.config.user-service.name}", url = "${feign.client.config.user-service.url}")
public interface UserClient {
    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId);
}