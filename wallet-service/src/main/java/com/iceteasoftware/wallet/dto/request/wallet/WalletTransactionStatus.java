package com.iceteasoftware.wallet.dto.request.wallet;

import com.iceteasoftware.wallet.enums.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionStatus {
    private String transactionCode; // Mã giao dịch
    private Stage stage; // Trạng thái ("DEDUCTED", "COMPLETED", "FAILED")
    private String senderWalletCode; // Mã ví người gửi
    private String recipientWalletCode; // Mã ví người nhận
    private Long amount; // Số tiền giao dịch
    private String errorMessage; // Thông báo lỗi (nếu có)
}
