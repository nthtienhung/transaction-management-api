package com.iceteasoftware.iam.client;

import com.iceteasoftware.common.security.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Annotation để chỉ định rằng đây là một Feign Client
/**
 * Feign Client để giao tiếp với dịch vụ người dùng.
 *
 * <p>Client này cho phép thực hiện các yêu cầu đến dịch vụ người dùng,
 * bao gồm việc tạo hồ sơ người dùng và kiểm tra số điện thoại đã tồn tại.</p>
 */

@FeignClient(name = "${feign.client.config.user-service.name}",
        url = "${feign.client.config.user-service.url}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface UserClient {

    /**
     * Kiểm tra xem số điện thoại đã tồn tại trong hệ thống hay chưa.
     *
     * @param phone số điện thoại cần kiểm tra
     * @return true nếu số điện thoại đã tồn tại, false nếu không
     */
    @GetMapping(value = "/check-phone-exists", produces = MediaType.APPLICATION_JSON_VALUE)
    Boolean checkPhoneExists(@RequestParam String phone);
}
