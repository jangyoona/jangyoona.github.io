package com.groupware.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ApprovalSaveDto {

    private int approvalSaveDocumentNo;
    private int empId;
    private Date approvalDate;
    private String approvalForm;
    private String approvalTitle;
    private String approvalContent;
    private String approvalActive;

    private String empName;
    private List<ApproverDto> approvers;

}
