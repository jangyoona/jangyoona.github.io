package com.groupware.mapper;

import com.groupware.dto.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    Integer getEmpIdByEmail(String email); // emp_id 조회 메서드 추가
    void insertNotification(@Param("dto") NotificationDto dto, @Param("empId") Integer empId);
    List<NotificationDto> selectNotificationByEmpId(@Param("empId") int empId);
    void updateNotificationStatusByNo(@Param("notificationNo") int notificationNo);
}
