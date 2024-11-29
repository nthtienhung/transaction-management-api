package com.iceteasoftware.notification.service.impl;

import com.iceteasoftware.notification.constant.KafkaTopicConstants;
import com.iceteasoftware.notification.entity.Notification;
import com.iceteasoftware.notification.entity.Template;
import com.iceteasoftware.notification.repository.NotificationRepository;
import com.iceteasoftware.notification.repository.TemplateRepository;
import com.iceteasoftware.notification.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceteasoftware.notification.util.ThreadLocalUtil;
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
        JsonNode jsonNode = objectMapper.readTree(message);
        String data = jsonNode.get("data").asText();
        String userId = jsonNode.get("userId").asText();

        ThreadLocalUtil.setCurrentUser(userId);

        JsonNode dataNode = objectMapper.readTree(data);
        String email = dataNode.get("email").asText();
        String otp = dataNode.get("otp").asText();

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
        ThreadLocalUtil.remove();
        log.info("OTP has been sent to email: {}", email);
    }

    @Override
    @Transactional
    @KafkaListener(
            topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_FORGOT_PASSWORD,
            groupId = "send-email-forgot-password-group")
    public void sendOTPForgotPassword(String message) throws JsonProcessingException, MessagingException {
        // 1. Parse tin nhắn Kafka
        JsonNode jsonNode = objectMapper.readTree(message);
        String data = jsonNode.get("data").asText();
        JsonNode dataNode = objectMapper.readTree(data);
        String userId = jsonNode.get("userId").asText();

        ThreadLocalUtil.setCurrentUser(userId);

        String email = dataNode.get("email").asText();
        String otp = dataNode.get("otp").asText();

        // 2. Tạo email MIME
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        // 3. Tạo thông tin cho email template
        Context context = new Context();
        Map<String, Object> props = new HashMap<>();
        props.put("email", email);
        props.put("otp", otp);

        // 4. Lưu template vào database
        String jsonString = objectMapper.writeValueAsString(props);
        Map<String, Object> parameterMap = objectMapper.readValue(jsonString, new TypeReference<>() {});
        templateRepository.save(Template.builder()
                .type("FORGOT_PASSWORD")
                .parameter(parameterMap)
                .build());

        // 5. Lưu thông báo trạng thái vào database
        notificationRepository.save(Notification.builder()
                .work("FORGOT_PASSWORD")
                .status("SUCCESS")
                .build());

        // 6. Render email từ template
        context.setVariables(props);
        mimeMessageHelper.setFrom(emailFrom);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Reset Your Password");
        String html = templateEngine.process("forgot-password-email", context);
        mimeMessageHelper.setText(html, true);

        // 7. Gửi email
        mailSender.send(mimeMessage);
        ThreadLocalUtil.remove();
        log.info("Password reset email has been sent to: {}", email);
    }
}
