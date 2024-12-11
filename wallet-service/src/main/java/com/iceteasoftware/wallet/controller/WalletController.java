package com.iceteasoftware.wallet.controller;

import com.iceteasoftware.wallet.dto.response.WalletResponse;
import com.iceteasoftware.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:52 AM
 */

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

//    @PostMapping()
//    public ResponseObject<String> createWallet(@RequestBody CreateWalletRequest request){
//        walletService.createWallet(request);
//        return new ResponseObject<>(HttpStatus.CREATED.value(), Constants.DEFAULT_MESSAGE_CREATE_SUCCESS, LocalDateTime.now());
//    }

    @GetMapping("/{walletCode}")
    WalletResponse getWalletByWalletCode(@PathVariable("walletCode") String walletCode){
        System.out.println("Wallet Code: " + walletCode);
        return walletService.getWalletByCode(walletCode);
    }

    @PutMapping("/{walletCode}/balance")
    void updateWalletBalance(@PathVariable("walletCode") String walletCode, @RequestBody Long amount){
        System.out.println("Wallet Code: " + walletCode);
        walletService.updateWalletBalance(walletCode, amount);
    }
    @GetMapping("/getWallet/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable("userId") String userId){
        System.out.println("User ID: " + userId);
        WalletResponse walletResponse = walletService.getWalletByUserId(userId);
        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }
}
