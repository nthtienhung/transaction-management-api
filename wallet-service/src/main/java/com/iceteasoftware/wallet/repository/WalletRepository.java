package com.iceteasoftware.wallet.repository;

import com.iceteasoftware.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:46 AM
 */

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByWalletCode(String walletCode);

    @Query("SELECT w.userId FROM Wallet w WHERE w.walletCode = :walletCode")
    String findUserIdByWalletCode(String walletCode);

    Wallet findByUserId(String userId);
}
