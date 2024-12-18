package com.iceteasoftware.notification.service.impl;

import com.iceteasoftware.notification.constant.KafkaTopicConstants;
import com.iceteasoftware.notification.dto.TransactionStatsResponse;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


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

    @Override
    @Transactional
    @KafkaListener(
            topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_OTP,
            groupId = "send-email-transaction-otp-group")
    public void sendTransactionOTP(String message) throws JsonProcessingException, MessagingException {
        JsonNode jsonNode = objectMapper.readTree(message);
        String data = jsonNode.get("data").asText();
        JsonNode dataNode = objectMapper.readTree(data);

        String userId = jsonNode.get("userId").asText();

        ThreadLocalUtil.setCurrentUser(userId);

        String email = dataNode.get("email").asText();
        String otp = dataNode.get("otp").asText();
        String amount = dataNode.get("amount").asText();

        // Gửi email OTP
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
        props.put("amount", amount);

        String jsonString = objectMapper.writeValueAsString(props);
        Map<String, Object> parameterMap = objectMapper.readValue(jsonString, new TypeReference<>() {
        });
        templateRepository.save(Template.builder()
                .type("SEND_TRANSACTION_OTP")
                .parameter(parameterMap)
                .build());

        notificationRepository.save(Notification.builder()
                .work("OTP_TRANSACTION")
                .status("SUCCESS")
                .build());

        context.setVariables(props);
        mimeMessageHelper.setFrom(emailFrom);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Transaction OTP Verification");
        String html = templateEngine.process("transaction-otp-email", context);
        mimeMessageHelper.setText(html, true);

        mailSender.send(mimeMessage);
        ThreadLocalUtil.remove();
        log.info("Transaction OTP email sent to: {}", email);
    }

    @Override
    @Transactional
    @KafkaListener(
            topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SUCCESSFUL_TRANSACTION,
            groupId = "send-email-successful-transaction-group")
    public void sendSuccessfulTransactionEmail(String message) throws JsonProcessingException, MessagingException {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String data = Optional.ofNullable(jsonNode.get("data"))
                    .map(JsonNode::asText)
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'data' field"));

            JsonNode dataNode = objectMapper.readTree(data);

            String userId = Optional.ofNullable(jsonNode.get("userId"))
                    .map(JsonNode::asText)
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'userId' field"));
            ThreadLocalUtil.setCurrentUser(userId);

            // Lấy thông tin từ dataNode
            String senderEmail = dataNode.get("senderMail").asText();
            String senderWalletCode = dataNode.get("senderWalletCode").asText();
            String recipientEmail = dataNode.get("recipientMail").asText();
            String recipientWalletCode = dataNode.get("recipientWalletCode").asText();
            String transactionCode = dataNode.get("transactionCode").asText();
            String amount = dataNode.get("amount").asText();
            String description = dataNode.get("description").asText();

            // Gửi email
            sendTransactionEmail(senderEmail, transactionCode, amount, recipientWalletCode, description,
                    "Transaction Successful Notification", "successful-transaction-email");
            sendTransactionEmail(recipientEmail, transactionCode, amount, senderWalletCode, description,
                    "You Have Received a Transaction", "receive-transaction-email");

            ThreadLocalUtil.remove();

        } catch (Exception e) {
            log.error("Error processing Kafka message", e);
            throw e; // Rethrow để Kafka xử lý lại nếu cần
        }
    }

    private void sendTransactionEmail(String email, String transactionCode, String amount, String walletCode, String description, String subject, String templateName) throws MessagingException, JsonProcessingException {
        // 1. Tạo email MIME
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        String type = templateName.equalsIgnoreCase("successful-transaction-email") ? "SEND_EMAIL_TRANSACTION" : "RECEIVE_EMAIL_TRANSACTION";
        String work = templateName.equalsIgnoreCase("successful-transaction-email") ? "SEND_TRANSACTION" : "RECEIVE_TRANSACTION";

        // 2. Tạo thông tin cho email template
        Context context = new Context();
        Map<String, Object> props = new HashMap<>();
        props.put("email", email);
        props.put("transactionCode", transactionCode);
        props.put("amount", amount);
        props.put("walletCode", walletCode);
        props.put("description", description);

        String jsonString = objectMapper.writeValueAsString(props);
        Map<String, Object> parameterMap = objectMapper.readValue(jsonString, new TypeReference<>() {
        });
        templateRepository.save(Template.builder()
                .type(type)
                .parameter(parameterMap)
                .build());

        notificationRepository.save(Notification.builder()
                .work(work)
                .status("SUCCESS")
                .build());

        // 3. Render email từ template
        context.setVariables(props);
        mimeMessageHelper.setFrom(emailFrom);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);
        String html = templateEngine.process(templateName, context);
        mimeMessageHelper.setText(html, true);

        // 4. Gửi email
        mailSender.send(mimeMessage);
        log.info("Email has been sent to: {}", email);
    }

    @Override
    public void sendEmail(String email, List<TransactionStatsResponse> transactionDetails, String subject, String templateName, String timePeriod) throws MessagingException, JsonProcessingException, IOException {
        // 1. Tạo email MIME
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        // 2. Tạo thông tin cho email template
        Context context = new Context();
        Map<String, Object> props = new HashMap<>();

        // Tính tổng số giao dịch, tổng số tiền, số lượng giao dịch theo trạng thái
        long totalAmount = 0;
        long successCount = 0;
        long failedCount = 0;
        long pendingCount = 0;

        long successAmount = 0;
        long failedAmount = 0;
        long pendingAmount = 0;

        for (TransactionStatsResponse detail : transactionDetails) {
            totalAmount += detail.getAmount();
            switch (detail.getStatus()) {
                case "SUCCESS":
                    successCount++;
                    successAmount += detail.getAmount();
                    break;
                case "FAILED":
                    failedCount++;
                    failedAmount += detail.getAmount();
                    break;
                case "PENDING":
                    pendingCount++;
                    pendingAmount += detail.getAmount();
                    break;
            }
        }

        // Chuyển các thông tin này vào props để hiển thị trong email
        props.put("totalTransactions", transactionDetails.size());
        props.put("totalAmount", totalAmount);
        props.put("successCount", successCount);
        props.put("failedCount", failedCount);
        props.put("pendingCount", pendingCount);
        props.put("successAmount", successAmount);  // Tổng số tiền giao dịch thành công
        props.put("failedAmount", failedAmount);    // Tổng số tiền giao dịch thất bại
        props.put("pendingAmount", pendingAmount);  // Tổng số tiền giao dịch pending
        props.put("timePeriod", timePeriod);  // Thêm thời gian vào context

        // Tạo một danh sách các giao dịch để truyền vào template cho chi tiết giao dịch trong Excel
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> transactions = new ArrayList<>();
        transactionDetails.forEach(detail -> {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("transactionCode", detail.getTransactionCode());
            transaction.put("senderWalletCode", detail.getSenderWalletCode());
            transaction.put("recipientWalletCode", detail.getRecipientWalletCode());
            transaction.put("amount", detail.getAmount());
            transaction.put("status", detail.getStatus());
            ZonedDateTime zonedDateTime = detail.getCreatedDate().atZone(ZoneId.systemDefault());
            String formattedDate = zonedDateTime.format(formatter);
            transaction.put("createdDate", formattedDate);
            transactions.add(transaction);
        });

        // Thêm danh sách giao dịch vào props để truyền vào template
        props.put("transactions", transactions);

        // 3. Tạo tệp Excel
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");
            int rowNum = 0;

            // Tạo tiêu đề cho bảng
            Row headerRow = sheet.createRow(rowNum++);
            String[] columns = {"Transaction Code", "Amount", "Sender Wallet", "Recipient Wallet", "Status", "Created Date"};
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            // Thêm dữ liệu vào bảng
            for (TransactionStatsResponse transaction : transactionDetails) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transaction.getTransactionCode());
                row.createCell(1).setCellValue(transaction.getAmount());
                row.createCell(2).setCellValue(transaction.getSenderWalletCode());
                row.createCell(3).setCellValue(transaction.getRecipientWalletCode());
                row.createCell(4).setCellValue(transaction.getStatus());
                ZonedDateTime zonedDateTime = transaction.getCreatedDate().atZone(ZoneId.systemDefault());
                String formattedDate = zonedDateTime.format(formatter);
                row.createCell(5).setCellValue(formattedDate);
            }

            // Ghi workbook vào ByteArrayOutputStream
            workbook.write(byteArrayOutputStream);
        }

        // 4. Đính kèm tệp Excel vào email
        byte[] excelData = byteArrayOutputStream.toByteArray();
        mimeMessageHelper.addAttachment("Transactions_Report.xlsx", new ByteArrayResource(excelData));

        // 5. Cấu hình nội dung email
        context.setVariables(props);  // Thiết lập các biến cho template
        mimeMessageHelper.setFrom(emailFrom);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);
        String html = templateEngine.process(templateName, context);
        mimeMessageHelper.setText(html, true);

        // 6. Gửi email
        mailSender.send(mimeMessage);
        log.info("Email has been sent to: {}", email);
    }

}
