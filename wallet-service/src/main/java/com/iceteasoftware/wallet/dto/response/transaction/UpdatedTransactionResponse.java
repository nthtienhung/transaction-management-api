package com.iceteasoftware.wallet.dto.response.transaction;

import com.iceteasoftware.wallet.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatedTransactionResponse {
    private String transactionCode;

    private Status status;
}
