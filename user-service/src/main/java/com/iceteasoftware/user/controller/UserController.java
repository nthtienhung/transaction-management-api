package com.iceteasoftware.user.controller;


import com.iceteasoftware.user.constant.Constants;
import com.iceteasoftware.user.dto.UserProfileResponse;
import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.UserResponse;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.dto.response.profile.FullNameResponse;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.entity.User;
import com.iceteasoftware.user.enums.MessageCode;
import com.iceteasoftware.user.enums.Status;
import com.iceteasoftware.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final String ADMIN_AUTHORITY = "hasRole('ROLE_ADMIN')";
    private final UserService userService;

    /**
     * API to retrieve the user's profile based on the JWT provided in the Authorization header.
     *
     * @param request the HTTP request containing the JWT in the Authorization header.
     * @return a {@link ResponseEntity} containing the user's profile if found,
     * or an error message if the JWT is invalid or the user is not found.
     */
    @GetMapping("/profile")
    public ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request) {
        return this.userService.getProfile(request);
    }

    @GetMapping("/check-phone-exists")
    public Boolean isPhoneExists(@RequestParam String phone) {
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
     * or an appropriate error message if the update fails.
     */
    @PutMapping("/profile")
    public ResponseEntity<ResponseObject<Profile>> updateProfile(
            HttpServletRequest request,
            @RequestBody CreateProfileRequest updateRequest) {
        return userService.updateProfile(request, updateRequest);
    }


    @GetMapping("/getUser")
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        return this.userService.findUser(request);
    }
    
    /**
     * Gets all user profiles for admin dashboard
     * 
     * @return List of all user profiles
     */

    @GetMapping("/user-list")
    @PreAuthorize(ADMIN_AUTHORITY)
    public ResponseObject<Page<UserProfileResponse>> getUserList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String searchTerm,
        @RequestParam(defaultValue = "createDate") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<UserProfileResponse> data = userService.getAllUserProfile(pageRequest, searchTerm);
        return new ResponseObject<>(
            HttpStatus.OK.value(), 
            Constants.DEFAULT_MESSAGE_SUCCESS, 
            LocalDateTime.now(), 
            data
        );
    }

    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId) {
        System.out.println("User ID: " + userId);
        UserResponse userResponse = userService.getUserById(userId);
        System.out.println("User Response: " + userResponse);
        return userResponse;
    }

    @GetMapping("/check-email-exists")
    Boolean isEmailExists(@RequestParam String email) {
        return userService.isEmailExists(email);
    }

    @GetMapping("/{userId}/full-name")
    public FullNameResponse getFullNameByUserId(@PathVariable("userId") String userId){
        return userService.getFullNameByUserId(userId);
    }

    @GetMapping("/user-id")
    public String getUserIdByUsername(@RequestParam String username){
        return userService.getUserIdByUsername(username);
    }

}
