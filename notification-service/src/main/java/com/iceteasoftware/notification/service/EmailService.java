package com.iceteasoftware.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendOTPSignUp(String message) throws JsonProcessingException, MessagingException;

    void sendOTPForgotPassword(String message) throws JsonProcessingException, MessagingException;

}
