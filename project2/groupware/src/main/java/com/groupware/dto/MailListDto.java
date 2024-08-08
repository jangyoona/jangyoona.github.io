package com.groupware.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class MailListDto {

    private ArrayList<Mail> mails; // 여러 개의 Mail 객체를 담는 리스트
    private int emailNo;
    public MailListDto() {
        this.mails = new ArrayList<>(); // 빈 ArrayList 초기화
    }

}