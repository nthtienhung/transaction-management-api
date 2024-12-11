package com.iceteasoftware.iam.controller;

import com.iceteasoftware.iam.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("get-role-status/{userId}")
    public StatusRoleUserResponse getRoleStatus(@PathVariable String userId) {
        return userService.getRoleAndStatusByUserId(userId);
    }

}
