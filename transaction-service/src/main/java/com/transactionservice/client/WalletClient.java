package com.transactionservice.client;

import com.transactionservice.dto.request.UpdateWalletRequest;
import com.transactionservice.dto.response.wallet.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${feign.client.config.wallet-service.name}", url = "${feign.client.config.wallet-service.url}")
public interface WalletClient {
    @GetMapping("/{walletCode}")
    WalletResponse getWalletByWalletCode(@PathVariable("walletCode") String walletCode);

    @PutMapping("/{walletCode}/balance")
    void updateWalletBalance(@PathVariable("walletCode") String walletCode, @RequestBody Long amount);

    @GetMapping("/{walletCode}/user-id")
    String getUserIdByWalletCode(@PathVariable("walletCode") String walletCode);

//    @PostMapping("/deduct")
//    ResponseEntity<?> deductBalance(@RequestBody WalletTransactionRequest request);

    @PostMapping("/rollback")
    ResponseEntity<?> rollbackBalance(@RequestBody UpdateWalletRequest request);
}
