package com.example.iamservice.controller;

import com.example.iamservice.dto.request.changepassword.ChangePasswordRequest;
import com.example.iamservice.dto.response.ResponseData;
import com.example.iamservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;

    /**
     * Method dùng để đổi mật khẩu user bằng email
     *
     * @param request
     */
    @PostMapping("/change-password")
    public ResponseEntity<ResponseData> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePasswordByEmail(request);
    }
}
