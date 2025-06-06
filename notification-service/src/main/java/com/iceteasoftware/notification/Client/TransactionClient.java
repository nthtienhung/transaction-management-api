package com.iceteasoftware.notification.Client;

import com.iceteasoftware.notification.dto.TransactionStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;

@FeignClient(name = "transaction-service", url = "${feign.client.config.transaction-service.url}")
public interface TransactionClient {
    @GetMapping("/transactions-detail")
    List<TransactionStatsResponse> getTransactionDetails(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate);
}

