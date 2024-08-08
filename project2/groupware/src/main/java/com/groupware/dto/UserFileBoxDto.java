package com.groupware.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserFileBoxDto {

    private int fileNo;
    private int empId;
    private String userFileName;
    private String savedFileName;
    private String note;
    private Timestamp saveDate;
    private String fileSort;

}
