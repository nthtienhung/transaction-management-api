package com.iceteasoftware.iam.controller;

import com.iceteasoftware.iam.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.iam.enums.Status;
import com.iceteasoftware.iam.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("get-role-status/{userId}")
    public StatusRoleUserResponse getRoleStatus(@PathVariable String userId) {
        return userService.getRoleAndStatusByUserId(userId);
    }


    @PutMapping("update-status/{userId}")
    @PreAuthorize("hasRole('ADMIN')")  // Make sure only admins can update status
    @CrossOrigin(origins = "http://localhost:3000")  // Add this to allow frontend calls
    public void updateUserStatus(
        @PathVariable String userId,
        @RequestBody Status status
    ) {
        userService.updateUserStatus(userId, status);
    }
}
