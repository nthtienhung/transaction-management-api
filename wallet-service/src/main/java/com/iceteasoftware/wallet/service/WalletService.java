package com.iceteasoftware.wallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:47 AM
 */
public interface WalletService {

    void createWallet(String createWalletMessage) throws JsonProcessingException;

}
