package com.groupware.service;

import com.groupware.dto.MailAttachDto;
import com.groupware.dto.MailDto;
import com.groupware.dto.PositionDto;

import java.util.List;
import java.util.Map;

public interface MailService {

    void writeMail(String recipient, MailDto mail, List<MailAttachDto> attachments);

    Map<String, Integer> getMailCountByStatus(String empEmail);
    int getMailCount(String empEmail, String type, String searchText);
    List<MailDto> findMailByRange(int start, int pageSize, String empEmail, String type, String searchText);
    MailDto findMailByMailNo(int emailNo, String empEmail);

    void updateFromEmails(List<Long> fromEmailNos, String type);
    void updateToEmails(List<Long> toEmailNos, String type);
    MailDto  findDetailMailByMailNo(int emailNo, String empEmail, String type);

    MailAttachDto findMailAttachByAttachNo(int attachNo);

    PositionDto getPositionNameByPositionNo(int positionNo);
}
