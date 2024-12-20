package com.iceteasoftware.user.client;

import com.iceteasoftware.user.configuration.security.AuthenticationRequestInterceptor;
import com.iceteasoftware.user.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.user.enums.Status;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "iam-service", url = "localhost:8081/auth",
    configuration = { AuthenticationRequestInterceptor.class })
public interface IamClient {

    @GetMapping("/get-role-status/{userId}")
    StatusRoleUserResponse getRoleStatus(@PathVariable("userId") String userId);

    // @PutMapping("update-status/{userId}")
    // void updateUserStatus(
    //         @PathVariable("userId") String userId,
    //         @RequestBody Status status,
    //         @RequestHeader("Authorization") String token,  // Add this
    //         @RequestHeader("X-Role") String role  // Add this
    // );
}
