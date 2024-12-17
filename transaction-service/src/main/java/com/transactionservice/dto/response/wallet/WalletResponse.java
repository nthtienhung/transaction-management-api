package com.transactionservice.dto.response.wallet;

import lombok.Data;

@Data
public class WalletResponse {

    private String walletCode;

    private Long balance;

    private String userId;
}
