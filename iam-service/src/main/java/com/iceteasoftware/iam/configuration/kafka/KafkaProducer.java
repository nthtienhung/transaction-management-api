package com.iceteasoftware.iam.configuration.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceteasoftware.iam.constant.KafkaTopicConstants;
import com.iceteasoftware.iam.dto.request.email.EmailDTORequest;
import com.google.gson.Gson;
import com.iceteasoftware.iam.dto.request.signup.CreateProfileRequest;
import com.iceteasoftware.iam.dto.request.wallet.CreateWalletRequest;
import com.iceteasoftware.iam.configuration.JacksonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageEmail(EmailDTORequest emailDTO) {
        Gson gson = new Gson();
        String msg = gson.toJson(emailDTO);
        log.info("Sending message : {}", msg);
        kafkaTemplate.send(emailDTO.getTopicName(),msg);
    }

    public void sendMessageCreateProfile(CreateProfileRequest request) throws JsonProcessingException {
        ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
        String message = objectMapper.writeValueAsString(request);
        log.info("Sending create profile request message : {}", message);
        kafkaTemplate.send(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_PROFILE, message);
    }

    public void sendMessageCreateWallet(CreateWalletRequest request) throws JsonProcessingException {
        ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
        String message = objectMapper.writeValueAsString(request);
        log.info("Sending create wallet request message message : {}", message);
        kafkaTemplate.send(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_WALLET, message);
    }

}
