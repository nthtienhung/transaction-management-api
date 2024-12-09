package com.transactionservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Table(name = "tbl_transaction", schema = "transaction_service")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @Column(name = "transaction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;

    @Column(name = "transaction_code", length = 255, nullable = false, unique = true)
    private String transactionCode;

    @Column(name = "sender_wallet_id", length = 255, nullable = false)
    private String senderWalletId;

    @Column(name = "receiver_wallet_id", length = 255, nullable = false)
    private String receiverWalletId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "status", length = 255, nullable = false)
    private String status;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
