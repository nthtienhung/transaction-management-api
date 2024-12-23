package com.iceteasoftware.iam.controller;

import com.iceteasoftware.iam.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final String ADMIN_AUTHORITY = "hasRole('ROLE_ADMIN')";
    private final AdminService adminService;

    @PreAuthorize(ADMIN_AUTHORITY)
    @GetMapping("/admin/emails")
    public ResponseEntity<List<String>> getAdminEmails() {
        return ResponseEntity.ok(adminService.getAllAdminEmails());
    }
}
