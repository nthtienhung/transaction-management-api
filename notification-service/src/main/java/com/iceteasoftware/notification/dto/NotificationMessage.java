package com.iceteasoftware.notification.dto;

/**
 * Author: Tran Duc Thinh
 * Date: 11/29/2024
 * Time: 10:14 AM
 */
public class NotificationMessage {
    private String userId;
    private String message;

    public NotificationMessage() {
    }

    public NotificationMessage(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
