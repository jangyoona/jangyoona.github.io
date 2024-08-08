package com.groupware.dto;

import lombok.Data;
import lombok.Setter;

import java.util.Date;

@Data
public class ProjectBoardDto {

    private int scheduleBoardNo;
    private int scheduleNo;
    private String scheduleBoardContent;
    private Date scheduleBoardWriteDate;
    private Date scheduleBoardMeetingDate;
    private int scheduleCategoryNo;
    private boolean scheduleBoardActive;
    private int empId;

    // 부서
    private String deptName;

    private EmployeeDto employee;

}
