package com.iceteasoftware.wallet.controller;

import com.iceteasoftware.wallet.dto.response.WalletResponse;
import com.iceteasoftware.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:52 AM
 */

@RestController
@RequiredArgsConstructor
public class WalletController {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    private final WalletService walletService;

    @PreAuthorize("hasRole('" + ROLE_USER + "')")
    @GetMapping("/code/{userId}")
    WalletResponse getWalletByUserId(@PathVariable("userId") String userId) {
        System.out.println("User Id: " + userId);
        return walletService.getWalletByUserId(userId);
    }

    @PreAuthorize("hasRole('" + ROLE_USER + "')")
    @GetMapping("/{walletCode}")
    WalletResponse getWalletByWalletCode(@PathVariable("walletCode") String walletCode) {
        System.out.println("Wallet Code: " + walletCode);
        return walletService.getWalletByCode(walletCode);
    }

    @PreAuthorize("hasRole('" + ROLE_USER + "')")
    @PutMapping("/{walletCode}/balance")
    void updateWalletBalance(@PathVariable("walletCode") String walletCode, @RequestBody Long amount) {
        System.out.println("Wallet Code: " + walletCode);
        walletService.updateWalletBalance(walletCode, amount);
    }

    @PreAuthorize("hasRole('" + ROLE_USER + "')")
    @GetMapping("/{walletCode}/user-id")
    public String getUserIdByWalletCode(@PathVariable("walletCode") String walletCode) {
        return walletService.getUserIdByWalletCode(walletCode);
    }

    @PreAuthorize("hasRole('" + ROLE_USER + "')")
    @GetMapping("/getWallet/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable("userId") String userId) {
        System.out.println("User ID: " + userId);
        WalletResponse walletResponse = walletService.getWalletByUserId(userId);
        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }
}
