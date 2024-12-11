package com.iceteasoftware.wallet.repository;

import com.iceteasoftware.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:46 AM
 */

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Wallet findByWalletCode(String walletCode);
    Wallet findByUserId(String userId);
}
