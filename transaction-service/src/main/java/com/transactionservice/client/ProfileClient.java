//package com.transactionservice.client;
//
//import com.transactionservice.dto.response.ProfileResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@FeignClient(name = "${feign.client.config.profile-service.name}", url = "${feign.client.config.profile-service.url}")
//public interface ProfileClient {
//    /**
//     * Lấy thông tin hồ sơ theo ID.
//     *
//     * @param profileId ID của hồ sơ.
//     * @return Thông tin hồ sơ người dùng.
//     */
//    @GetMapping("/profiles/{profileId}")
//    ProfileResponse getProfileById(@PathVariable("profileId") String profileId);
//}
