package com.groupware.dto;

import lombok.Data;

@Data
public class EmployeeAttachDto {

    private int attachNo;
    private String userFilename;
    private String savedFilename;
    private int empId;

}
