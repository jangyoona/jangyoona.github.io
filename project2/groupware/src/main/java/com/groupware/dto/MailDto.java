package com.groupware.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class MailDto {

    private int emailNo;
    private int empId;
    private String emailFrom;
    private String emailTo;
    private String emailTitle;
    private String emailContent;
    private Date emailWriteDate;
    private boolean emailToRead;
    private boolean emailFromRead;
    private int emailFromActive;
    private int emailToActive;
    private boolean emailToLike;
    private boolean emailFromLike;
    private LocalDateTime emailFromDeleteDate;
    private LocalDateTime emailToDeleteDate;


    // email 테이블과 emailattach 테이블 사이의 1 : Many 관계를 구현하는 필드
    private List<MailAttachDto> attachments;
    private int emailAttachNo;
    private String emailUserFileName;
    private String emailSavedFileName;

    private String empPosition;
    private String empName;

    private String recipient;
}
