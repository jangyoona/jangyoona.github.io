package com.groupware.dto;

import lombok.Data;
import lombok.Setter;

import java.util.Date;

@Data
public class CommentDto {

    private int commentNo;
    private int empId;
    private int boardNo;
    private String commentContent;
    private Date commentWriteDate;
    private boolean commentActive;
    private int groupNo;
    private int stepNo;
    private int depthNo;
    private Date commentModifyDate;

}
