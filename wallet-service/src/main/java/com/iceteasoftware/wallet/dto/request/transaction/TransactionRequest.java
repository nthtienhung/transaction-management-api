package com.iceteasoftware.wallet.dto.request.transaction;

import com.iceteasoftware.wallet.dto.request.common.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest extends Request {

    private String senderWalletCode;

    private String recipientWalletCode;

    private Long amount;

    private String description;
}