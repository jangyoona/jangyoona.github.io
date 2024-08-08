package com.groupware.service;

import com.groupware.dto.NotificationDto;
import com.groupware.mapper.NotificationMapper;
import com.groupware.websocket.GroupwareWebSocketHandler;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

// @Service
public class NotificationServiceImpl implements NotificationService {

    @Setter
    private NotificationMapper notificationMapper;

    @Autowired
    private GroupwareWebSocketHandler groupwareWebSocketHandler;

    @Override
    public void saveNotification(NotificationDto notificationDto) {
        Integer empId = notificationMapper.getEmpIdByEmail(notificationDto.getRecipientEmail());
        notificationMapper.insertNotification(notificationDto, empId );
        try {
            groupwareWebSocketHandler.sendMessageToSomeone(notificationDto.getMessage(),empId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void saveBoardNotification(NotificationDto notificationDto) {
        Integer empId = notificationMapper.getEmpIdByEmail(notificationDto.getRecipientEmail());
        notificationMapper.insertNotification(notificationDto, empId );
        try {
            groupwareWebSocketHandler.sendMessageToAll(notificationDto.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public List<NotificationDto> getNotificationsByEmpId(int empId) {
        return notificationMapper.selectNotificationByEmpId(empId);
    }

    @Override
    public void deleteNotificationByNo(int notificationNo) {
        notificationMapper.updateNotificationStatusByNo(notificationNo);
    }
}
