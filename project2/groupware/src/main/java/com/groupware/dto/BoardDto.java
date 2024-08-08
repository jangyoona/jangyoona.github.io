package com.groupware.dto;

import lombok.Data;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
public class BoardDto {

    private int boardNo;
    private String boardTitle;
    private String boardContent;
    private Date boardWriteDate;
    private boolean boardActive;
    private int empId;
    private int boardCount;

    private List<BoardAttachDto> attachments;
    private int boardAttachNo;
    private String boardUserFileName;
    private String boardSavedFileName;

    private String empPosition;
    private String empName;

    private String recipient;

}
