package com.transactionservice.dto.request;

import com.transactionservice.enums.Stage;
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
