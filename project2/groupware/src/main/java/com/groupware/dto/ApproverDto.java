package com.groupware.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ApproverDto {

    private int approvalDocumentNo;
    private int empId;
    private int approvalSequence;
    private Date approvalDate;
    private String approvalActive;
    private String approvalComment;

    private String empPosition;
    private String empName;
    private String positionName;
    // 동적쿼리 연습
    private String type;
    private int mySequence;
    private int btnType;

    //결재선 저장
    private int saveLineNo;


    //결재선 불러오기
    private String deptName;
    private String lineTitle;
}
