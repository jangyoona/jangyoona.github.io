package com.groupware.dto;

import lombok.Data;

@Data
public class Mail {
    private Long emailNo;   // 이메일 번호
    private String emailFrom; // 발신자 이메일
    private String emailTo;   // 수신자 이메일


}