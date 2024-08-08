package com.groupware.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ApprovalDto {

    private int approvalDocumentNo;
    private int empId;
    private Date approvalDate;
    private String approvalForm;
    private String approvalTitle;
    private String approvalContent;
    private String approvalActive;
    private boolean active;

    //결재선 저장
    private int saveLineNo;
    private String lineTitle;
    // 임시저장, 결제요청
    private int actionType;

    private String empName;
    private List<ApproverDto> approvers;





}
