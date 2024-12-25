package com.transactionservice.dto.response.wallet;

import com.transactionservice.enums.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionStatus {
    private String transactionCode; // Mã giao dịch
    private Stage stage;
    private String senderWalletCode; // Mã ví người gửi
    private String recipientWalletCode; // Mã ví người nhận
    private Long amount; // Số tiền giao dịch
    private String errorMessage; // Thông báo lỗi (nếu có)
}
