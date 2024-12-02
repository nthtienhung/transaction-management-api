package com.iceteasoftware.user.controller;

import com.iceteasoftware.user.constant.Constants;
import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    /**
     * API to retrieve the user's profile based on the JWT provided in the Authorization header.
     *
     * @param request the HTTP request containing the JWT in the Authorization header.
     * @return a {@link ResponseEntity} containing the user's profile if found,
     *         or an error message if the JWT is invalid or the user is not found.
     */
    @GetMapping("/profile")
    public ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request) {
        return this.userService.getProfile(request);
    }

    @PostMapping("/profile")
    public com.iceteasoftware.user.dto.response.ResponseObject<String> createProfile(@RequestBody CreateProfileRequest request){
        userService.createProfile(request);
        return new com.iceteasoftware.user.dto.response.ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_SUCCESS, LocalDateTime.now());
    }

    @GetMapping("/check-phone-exists")
    public Boolean isPhoneExists(@RequestParam String phone){
        return userService.isPhoneExists(phone);
    }

    /**
     * Updates the user's profile based on the data provided in the request body.
     * The email of the user is extracted from the JWT provided in the request header
     * and cannot be modified through this method.
     *
     * @param request       the {@link HttpServletRequest} containing the JWT in the Authorization header.
     * @param updateRequest the {@link CreateProfileRequest} containing the updated profile details.
     *                      Note: The email field in the request will be ignored.
     * @return a {@link ResponseEntity} containing a {@link ResponseObject} with the updated profile if successful,
     *         or an appropriate error message if the update fails.
     */
    @PutMapping("/profile")
    public ResponseEntity<ResponseObject<Profile>> updateProfile(
            HttpServletRequest request,
            @RequestBody CreateProfileRequest updateRequest) {
        return userService.updateProfile(request, updateRequest);
    }

}
