package com.groupware.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class ProjectUserScheduleDto {

    private int userScheduleNo;
    private int empId;
    private int scheduleNo;
    private String userScheduleContent;
    private Date userScheduleWriteDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "y-M-d H:m")
    private Timestamp userScheduleStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "y-M-d H:m")
    private Timestamp userScheduleEndDate;
    private boolean userScheduleActive;
    private String userScheduleStatus;

    private ProjectDto project;
    private EmployeeDto employee;

}
