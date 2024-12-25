package com.transactionservice.constant;

public interface KafkaTopicConstants {

    public static final int DEFAULT_KAFKA_PARTITIONS = 5;
    public static final String DEFAULT_KAFKA_TOPIC_SEND_EMAIL_OTP = "send-email-otp";
    public static final String DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION = "send-email-successful-transaction";
    public static final String DEFAULT_KAFKA_TOPIC_SEND_EMAIL_RECEIVE_TRANSACTION = "send-email-receive-transaction";
    public static final String DEFAULT_KAFKA_TOPIC_CREATE_TRANSACTION = "create-transaction";
    public static final String DEFAULT_KAFKA_TOPIC_SUCCESSFUL_UPDATE_WALLET = "successful-update-wallet";
    public static final String DEFAULT_KAFKA_TOPIC_FAILED_UPDATE_WALLET = "failed-update-wallet";
    public static final String DEFAULT_KAFKA_TOPIC_SUCCESSFUL_DEDUCT_WALLET = "successful-deduct-wallet";
    public static final String DEFAULT_KAFKA_TOPIC_CREDIT_WALLET = "credit-wallet";
    public static final String DEFAULT_KAFKA_TOPIC_SUCCESSFUL_CREDIT_WALLET = "successful-credit-wallet";
    public static final String DEFAULT_KAFKA_TOPIC_COMPENSATION = "compensation";
    public static final String DEFAULT_KAFKA_TOPIC_SEND_REPORT = "send-report";
}
