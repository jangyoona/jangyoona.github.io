package com.groupware.dto;

import lombok.Data;
import java.util.List;

@Data
public class CalendarDto {
    private int calendarNo;
    private String calendarTitle;
    private String type;
    private int empId;
    private int deptNo;
    private List<CalendarEventDto> events; // 이벤트 리스트 추가
}
//CREATE TABLE calendar (
//        calendar_no INT AUTO_INCREMENT PRIMARY KEY,
//        calendar_title VARCHAR(255) NOT NULL,
//type ENUM('personal', 'department') NOT NULL,
//emp_id INT,
//dept_no INT,
//FOREIGN KEY (emp_id) REFERENCES employee(emp_id) ON DELETE CASCADE ON UPDATE CASCADE,
//FOREIGN KEY (dept_no) REFERENCES department(dept_no) ON DELETE CASCADE ON UPDATE CASCADE
//);
//
//CREATE TABLE calendar_event (
//        event_id INT AUTO_INCREMENT PRIMARY KEY,
//        event_title VARCHAR(255) NOT NULL,
//start DATETIME NOT NULL,
//end DATETIME,
//all_day BOOLEAN DEFAULT FALSE,
//background_color VARCHAR(7),
//border_color VARCHAR(7),
//url VARCHAR(255),
//calendar_no INT,
//FOREIGN KEY (calendar_no) REFERENCES calendar(calendar_no) ON DELETE CASCADE ON UPDATE CASCADE
//);
