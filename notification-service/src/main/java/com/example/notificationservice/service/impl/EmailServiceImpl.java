package com.example.notificationservice.service.impl;

import com.example.notificationservice.constant.KafkaTopicConstants;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.Template;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.repository.TemplateRepository;
import com.example.notificationservice.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.from}")
    private String emailFrom;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final TemplateRepository templateRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    @KafkaListener(topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SIGN_UP, groupId = "email-otp-group")
    public void sendOTPSignUp(String message) throws JsonProcessingException, MessagingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(message);
        String email = jsonNode.get("email").asText();
        String otp = jsonNode.get("otp").asText();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        Context context = new Context();
        Map<String, Object> props = new HashMap<>();
        props.put("email", email);
        props.put("otp", otp);
        String jsonString = objectMapper.writeValueAsString(props);
        Map<String, Object> parameterMap = objectMapper.readValue(jsonString, new TypeReference<>() {
        });
        templateRepository.save(Template.builder()
                .type("SEND_OTP_REGISTER")
                .parameter(parameterMap)
                .build());

        notificationRepository.save(Notification.builder()
                .work("REGISTER_USER")
                .status("SUCCESS")
                .build());

        context.setVariables(props);
        mimeMessageHelper.setFrom(emailFrom);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify your account");
        String html = templateEngine.process("otp-email", context);
        mimeMessageHelper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("OTP has been sent to email: {}", email);
    }
}
