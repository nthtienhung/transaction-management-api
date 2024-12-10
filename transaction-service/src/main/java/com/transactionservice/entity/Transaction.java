package com.transactionservice.entity;

import com.transactionservice.entity.common.AuditTable;
import com.transactionservice.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "tbl_transaction", schema = "transaction_service")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_code")
    private String transactionCode;

    @Column(name ="sender_wallet_code")
    private String senderWalletCode;

    @Column(name ="recipient_wallet_code")
    private String recipientWalletCode;

    @Column(name = "amount")
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "description")
    private String description;

}
