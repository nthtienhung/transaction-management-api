package com.iceteasoftware.user.client;

import com.iceteasoftware.user.dto.response.StatusRoleUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", url = "localhost:8081/auth")
public interface IamClient {

    @GetMapping("get-role-status/{userId}")
    StatusRoleUserResponse getRoleStatus(@PathVariable("userId") String userId);
}
