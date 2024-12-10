package com.iceteasoftware.wallet.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletResponse {

    private String walletCode;

    private Long balance;

    private String userId;
}
