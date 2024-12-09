package com.transactionservice.enums;

import com.transactionservice.configuration.message.LabelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageCode {
    // Server internal
    MSG1001(LabelKey.ERROR_INTERNAL_SERVER),

    // Bad request
    MSG1002(LabelKey.ERROR_BAB_REQUEST),

    // Email không đúng định dạng
    MSG1003(LabelKey.ERROR_EMAIL_IS_INVALID),

    // Email không tồn tại
    MSG1004(LabelKey.ERROR_EMAIL_NOT_EXIST),

    // Email không để null
    MSG1005(LabelKey.ERROR_EMAIL_IS_EMPTY),

    // Phone không để null
    MSG1006(LabelKey.ERROR_PHONE_IS_EMPTY),

    // Phone không đúng định dạng
    MSG1007(LabelKey.ERROR_PHONE_IS_INVALID),

    // PaymentMethod không để null
    MSG1008(LabelKey.ERROR_PAYMENT_METHOD_IS_EMPTY),

    // PaymentMethod không tồn tại
    MSG1009(LabelKey.ERROR_PAYMENT_METHOD_NOT_EXIST),

    // PaymentSource không để null
    MSG1010(LabelKey.ERROR_PAYMENT_SOURCE_IS_EMPTY),

    // PaymentSource không tồn tại
    MSG1011(LabelKey.ERROR_PAYMENT_SOURCE_NOT_EXIST),

    // TranStatus không để null
    MSG1012(LabelKey.ERROR_STATUS_IS_EMPTY),

    // TranStatus không tồn tại
    MSG1013(LabelKey.ERROR_STATUS_NOT_EXIST),

    // Amount không để null
    MSG1014(LabelKey.ERROR_AMOUNT_IS_EMPTY),

    // Amount phải lớn hơn 0
    MSG1015(LabelKey.ERROR_AMOUNT_MUST_BE_GREATER_THAN_ZERO),

    // Không có dũ liệu export data
    MSG1016(LabelKey.ERROR_NOT_DATA),


    // Thành công
    MSG2000(LabelKey.SUCCESS_DEFAULT),

    // Thêm thành công success successfully
    MSG2001(LabelKey.SUCCESS_CREATE),

    // Transaction không tồn tại
    MSG2002(LabelKey.ERROR_TRANSACTION_NOT_EXIST),
    ;

    private String key;

}
