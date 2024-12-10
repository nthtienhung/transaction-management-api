package com.iceteasoftware.wallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceteasoftware.wallet.dto.response.WalletResponse;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:47 AM
 */
public interface WalletService {

    void createWallet(String createWalletMessage) throws JsonProcessingException;

    WalletResponse getWalletByCode(String walletCode);

    void updateWalletBalance(String walletCode, Long amount);
}
