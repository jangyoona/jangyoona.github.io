package com.groupware.controller;

import com.groupware.service.NotificationService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = {"/notifications"})
public class NotificationController {

    @Setter(onMethod_ = {@Autowired})
    private NotificationService notificationService;

    @PostMapping(path = { "/delete" })
    public ResponseEntity<String> deleteNotification(@RequestParam("notificationId") int notificationId) {
        try {
            notificationService.deleteNotificationByNo(notificationId);
            return ResponseEntity.ok("알림이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제에 실패했습니다.");
        }
    }


}
