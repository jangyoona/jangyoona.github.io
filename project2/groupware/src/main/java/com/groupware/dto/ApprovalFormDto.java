package com.groupware.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ApprovalFormDto {

    private int approvalDocumentNo;
    private int approvalFormNo;
    private String approvalFormTitle;
    private String approvalFormDocument;
    private boolean documentActive;

}
