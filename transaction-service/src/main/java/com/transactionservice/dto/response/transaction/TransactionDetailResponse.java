package com.transactionservice.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * Author: thinhtd
 * Date: 12/16/2024
 * Time: 2:45 PM
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailResponse implements Serializable {

    private String transactionCode;
    private String senderWalletCode;
    private String recipientWalletCode;
    private Long amount;
    private String description;
    private String nameOfSender;
    private String nameOfRecipient;
    private String status;
    private Instant createdDate;
    private Instant updatedDate;

}
