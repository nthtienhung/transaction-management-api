//package com.iceteasoftware.wallet.configuration;
//
//import jakarta.annotation.PreDestroy;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class WalletServiceShutdownHandler {
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public WalletServiceShutdownHandler(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @PreDestroy
//    public void onShutdown() {
//        // Gửi message báo service shutdown
//        ServiceStatusEvent statusEvent = new ServiceStatusEvent();
//        statusEvent.setServiceName("WALLET_SERVICE");
//        statusEvent.setStatus(ServiceStatus.SHUTDOWN);
//
//        kafkaTemplate.send("service-status-topic", statusEvent);
//    }
//}
