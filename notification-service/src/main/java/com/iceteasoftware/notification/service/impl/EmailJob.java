package com.iceteasoftware.notification.service.impl;
import com.iceteasoftware.notification.Client.IAMClient;
import com.iceteasoftware.notification.Client.TransactionClient;
import com.iceteasoftware.notification.dto.TransactionStatsResponse;
import com.iceteasoftware.notification.service.EmailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class EmailJob implements Job {

    @Autowired
    private IAMClient iamClient;

    @Autowired
    private TransactionClient transactionClient;

    @Autowired
    private EmailService emailService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<String> adminEmails = iamClient.getAdminEmails();

        // Xác định khoảng thời gian lấy dữ liệu giao dịch (ví dụ: 24 giờ trước)
        Instant startDate = Instant.now().minus(24, ChronoUnit.HOURS);  // Lấy dữ liệu giao dịch 24h trước
        Instant endDate = Instant.now();  // Đến thời điểm hiện tại

        // Lấy thông tin giao dịch từ TransactionService
        List<TransactionStatsResponse> transactionDetails = transactionClient.getTransactionDetails(startDate, endDate);

        // Gửi email cho tất cả các admin
        adminEmails.forEach(email -> {
            try {
                emailService.sendEmail(email, transactionDetails, "Thống kê giao dịch", "transaction-stats-email");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

