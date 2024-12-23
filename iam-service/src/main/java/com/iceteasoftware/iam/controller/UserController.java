package com.iceteasoftware.iam.controller;

import com.iceteasoftware.iam.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.iam.enums.Status;
import com.iceteasoftware.iam.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get-role-status/{userId}")
    public StatusRoleUserResponse getRoleStatus(@PathVariable String userId) {
        return userService.getRoleAndStatusByUserId(userId);
    }


    @PutMapping("update-status/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> statusMap
    ) {
        Status status = Status.valueOf(statusMap.get("status"));
        userService.updateUserStatus(userId, status);
    }
}
