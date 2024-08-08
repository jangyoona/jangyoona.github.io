package com.groupware.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserBookmarkDto {
    private int empId;
    private String empName;
    private int contactId; // 즐찾당한 사람
    private String empEmail;
    private int deptNo;
    private String deptName;
    private String empPhone;
    private boolean bookmarkActive;

}
