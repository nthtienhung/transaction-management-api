package com.iceteasoftware.iam.client;

import com.iceteasoftware.iam.configuration.security.AuthenticationRequestInterceptor;
import com.iceteasoftware.iam.dto.request.wallet.CreateWalletRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:40 AM
 */

@FeignClient(name = "${feign.client.config.wallet-service.name}",
        url = "${feign.client.config.wallet-service.url}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface WalletClient {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseObject<String> createWallet(CreateWalletRequest request);

}
