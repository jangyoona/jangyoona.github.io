package com.groupware.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class NotificationDto {

    private int notificationNo;
    private String recipientEmail;
    private String message;
    private int empId;
    private Date timestamp;
    private Boolean notificationActive;
    private int notificationType;

    public NotificationDto(String recipientEmail, String message, int notificationType) {
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.notificationType = notificationType;
    }
}

