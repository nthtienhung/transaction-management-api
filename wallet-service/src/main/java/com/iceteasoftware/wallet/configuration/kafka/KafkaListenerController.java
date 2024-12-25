//package com.iceteasoftware.wallet.configuration.kafka;
//
////import org.springframework.kafka.listener.KafkaListenerEndpointRegistry;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class KafkaListenerController {
//
//    @Autowired
//    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
//
//    public void startListener() {
//        kafkaListenerEndpointRegistry.getListenerContainer("walletListener").start();
//        log.info("Kafka Listener started");
//    }
//
//    public void stopListener() {
//        kafkaListenerEndpointRegistry.getListenerContainer("walletListener").stop();
//        log.info("Kafka Listener stopped");
//    }
//}