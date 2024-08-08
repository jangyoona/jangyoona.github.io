package com.groupware.controller;

import com.groupware.dto.EmployeeDto;
import com.groupware.dto.NotificationDto;
import com.groupware.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Setter(onMethod_ = {@Autowired})
    private NotificationService notificationService;

    @ModelAttribute
    public void addNotifications(HttpSession session, Model model) {
        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        // 로그인 사용자가 null이 아닐 경우에만 알림을 추가
        if (employee != null) {
            List<NotificationDto> notifications = notificationService.getNotificationsByEmpId(employee.getEmpId());
            model.addAttribute("notifications", notifications);
        }
    }
}