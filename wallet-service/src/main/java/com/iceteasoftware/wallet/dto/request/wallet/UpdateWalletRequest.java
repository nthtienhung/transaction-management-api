package com.iceteasoftware.wallet.dto.request.wallet;

import com.iceteasoftware.wallet.enums.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletRequest {

    private String transactionCode;

    private String senderWalletCode;

    private String recipientWalletCode;

    private Stage stage;

    private Long amount;
}
