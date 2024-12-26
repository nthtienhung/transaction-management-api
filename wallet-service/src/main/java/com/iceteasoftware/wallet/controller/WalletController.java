package com.iceteasoftware.wallet.controller;

import com.iceteasoftware.wallet.dto.request.wallet.UpdateWalletRequest;
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

    private final String USER_AUTHORITY = "hasRole('ROLE_USER')";
    private final WalletService walletService;

    @PreAuthorize(USER_AUTHORITY)
    @GetMapping("/code/{userId}")
    WalletResponse getWalletByUserId(@PathVariable("userId") String userId) {
        System.out.println("User Id: " + userId);
        return walletService.getWalletByUserId(userId);
    }

    @GetMapping("/get-wallet/{walletCode}")
    WalletResponse getWalletByWalletCode(@PathVariable("walletCode") String walletCode) {
        System.out.println("Wallet Code: " + walletCode);
        return walletService.getWalletByCode(walletCode);
    }

    @PreAuthorize(USER_AUTHORITY)
    @PutMapping("/{walletCode}/balance")
    void updateWalletBalance(@PathVariable("walletCode") String walletCode, @RequestBody Long amount) {
        System.out.println("Wallet Code: " + walletCode);
        walletService.updateWalletBalance(walletCode, amount);
    }

    @GetMapping("/{walletCode}/user-id")
    public String getUserIdByWalletCode(@PathVariable("walletCode") String walletCode) {
        return walletService.getUserIdByWalletCode(walletCode);
    }

    @PreAuthorize(USER_AUTHORITY)
    @GetMapping("/getWallet/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable("userId") String userId) {
        System.out.println("User ID: " + userId);
        WalletResponse walletResponse = walletService.getWalletByUserId(userId);
        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }

//    @PostMapping("/deduct")
//    public ResponseEntity<?> deductBalance(@RequestBody WalletTransactionRequest request) {
//        walletService.deductBalance(request.getWalletCode(), request.getAmount());
//        return ResponseEntity.ok("Balance deducted successfully");
//    }

//    @PostMapping("/rollback")
//    public ResponseEntity<?> rollbackBalance(@RequestBody UpdateWalletRequest request) {
//        walletService.rollbackBalance(request);
//        return ResponseEntity.ok("Balance rolled back successfully");
//    }
}
