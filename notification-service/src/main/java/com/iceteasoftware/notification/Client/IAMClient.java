package com.iceteasoftware.notification.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "iam-service", url = "${feign.client.config.iam-service.url}")
public interface IAMClient {
    @GetMapping("/admin/emails")
    List<String> getAdminEmails();
}
