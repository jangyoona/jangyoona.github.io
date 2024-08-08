package com.groupware.service;

import com.groupware.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    void saveNotification(NotificationDto notificationDto);
    void saveBoardNotification(NotificationDto notificationDto);
    List<NotificationDto> getNotificationsByEmpId(int empId);
    void deleteNotificationByNo(int notificationNo);
}

