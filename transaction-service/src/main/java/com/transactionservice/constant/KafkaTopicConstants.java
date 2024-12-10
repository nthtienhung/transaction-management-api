package com.transactionservice.constant;

public interface KafkaTopicConstants {

    public static final int DEFAULT_KAFKA_PARTITIONS = 5;
    public static final String DEFAULT_KAFKA_TOPIC_SEND_EMAIL_OTP = "send-email-otp";
    public static final String DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION = "send-email-successful-transaction";
    public static final String DEFAULT_KAFKA_TOPIC_SEND_EMAIL_RECEIVE_TRANSACTION = "send-email-receive-transaction";
    public static final String DEFAULT_KAFKA_TOPIC_CREATE_TRANSACTION = "create-transaction";
}
