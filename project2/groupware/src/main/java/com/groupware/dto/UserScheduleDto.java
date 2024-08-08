package com.groupware.dto;

import lombok.Data;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class UserScheduleDto {

    private int personalScheduleNo;
    private Timestamp personalScheduleWriteDate;
    private Date personalScheduleStartDate;
    private Date personalScheduleEndDate;
    private String personalScheduleContent;
    private int empId;
}
