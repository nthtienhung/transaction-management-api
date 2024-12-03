package com.iceteasoftware.iam.configuration.kafka;


import com.iceteasoftware.iam.dto.request.email.EmailDTORequest;
import com.google.gson.Gson;
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

}
