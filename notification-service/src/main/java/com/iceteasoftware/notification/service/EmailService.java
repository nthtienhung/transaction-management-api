package com.iceteasoftware.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceteasoftware.notification.dto.TransactionStatsResponse;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface EmailService {

    void sendOTPSignUp(String message) throws JsonProcessingException, MessagingException;

    void sendOTPForgotPassword(String message) throws JsonProcessingException, MessagingException;

    void sendSuccessfulTransactionEmail(String message) throws JsonProcessingException, MessagingException;

    void sendTransactionOTP(String message) throws JsonProcessingException, MessagingException;

    void sendEmail(String email, List<TransactionStatsResponse> transactionDetails, String subject, String templateName, String timePeriod) throws MessagingException, JsonProcessingException, IOException;
}
