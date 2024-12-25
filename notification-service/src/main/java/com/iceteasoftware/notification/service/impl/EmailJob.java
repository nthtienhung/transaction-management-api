package com.iceteasoftware.notification.service.impl;

import com.iceteasoftware.notification.Client.IAMClient;
import com.iceteasoftware.notification.Client.TransactionClient;
import com.iceteasoftware.notification.dto.TransactionStatsResponse;
import com.iceteasoftware.notification.enums.ReportType;
import com.iceteasoftware.notification.service.EmailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.*;

import java.time.Instant;
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

        // Khởi tạo thời gian cho từng loại báo cáo
        Instant startDateDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant endDateDay = Instant.now();

        Instant startDateWeek = LocalDate.now()
                .with(DayOfWeek.MONDAY) // Thứ Hai đầu tuần
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant endDateWeek = LocalDate.now()
                .with(DayOfWeek.SUNDAY) // Chuyển về ngày Chủ Nhật cuối tuần
                .plusDays(1) // Thêm 1 ngày để tính đến cuối ngày Chủ Nhật
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant startDateMonth = LocalDate.now()
                .withDayOfMonth(1) // Ngày đầu tiên của tháng
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant endDateMonth = LocalDate.now()
                .withDayOfMonth(LocalDate.now().lengthOfMonth()) // Ngày cuối cùng của tháng
                .plusDays(1) // Thêm 1 ngày để tính đến cuối ngày cuối tháng
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();


        // Kiểm tra trigger để xác định loại báo cáo
        String triggerName = context.getTrigger().getKey().getName();
        switch (triggerName) {
            case "emailJobTriggerDaily":
                // Gửi báo cáo hàng ngày
                sendReportToAdmins(adminEmails, ReportType.DAILY, startDateDay, endDateDay);
                break;
            case "emailJobTriggerWeekly":
                // Gửi báo cáo hàng tuần
                sendReportToAdmins(adminEmails, ReportType.WEEKLY, startDateWeek, endDateWeek);
                break;
            case "emailJobTriggerMonthly":
                // Gửi báo cáo hàng tháng
                sendReportToAdmins(adminEmails, ReportType.MONTHLY, startDateMonth, endDateMonth);
                break;
            default:
                throw new JobExecutionException("Unknown trigger: " + triggerName);
        }
    }

    private void sendReportToAdmins(List<String> adminEmails, ReportType reportType, Instant startDate, Instant endDate) {
        List<TransactionStatsResponse> transactionDetails = transactionClient.getTransactionDetails(startDate, endDate);
        String subject = getSubjectByReportType(reportType);
        String period = getPeriodByReportType(reportType);

        // Gửi email cho tất cả admin
        adminEmails.forEach(email -> {
            try {
                emailService.sendEmail(email, transactionDetails, subject, "transaction-stats-email", period);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getSubjectByReportType(ReportType reportType) {
        switch (reportType) {
            case DAILY:
                return "Daily Transaction Report";
            case WEEKLY:
                return "Weekly Transaction Report";
            case MONTHLY:
                return "Monthly Transaction Report";
            default:
                return "Transaction Report";
        }
    }

    private String getPeriodByReportType(ReportType reportType) {
        switch (reportType) {
            case DAILY:
                return "Last 24 hours";
            case WEEKLY:
                return "Last 7 days";
            case MONTHLY:
                return "Last 30 days";
            default:
                return "Custom period";
        }
    }
}