package com.groupware.dto;

import lombok.Data;

@Data
public class ApproverConverterDto {

    private String empId;
    private String approvalSequence;
    private String approvalActive;
    private String approvalComment;

}
