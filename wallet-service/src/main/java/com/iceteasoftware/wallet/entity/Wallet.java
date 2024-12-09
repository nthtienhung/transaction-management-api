package com.iceteasoftware.wallet.entity;

import com.iceteasoftware.wallet.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:34 AM
 */

@Entity
@Table(name = "tbl_wallet", schema = "wallet_service")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Wallet extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id")
    private String id;

    @Column(name = "wallet_code", unique = true)
    private String walletCode;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "user_id")
    private String userId;

}
