package com.example.notificationservice.service;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface MailService {
    void sendOTP(String email) throws MessagingException, IOException;
}
