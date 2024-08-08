package com.groupware.service;

import com.groupware.dto.MailAttachDto;
import com.groupware.dto.MailDto;
import com.groupware.dto.PositionDto;
import com.groupware.mapper.MailMapper;
import lombok.Setter;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public class MailServiceImpl implements MailService {

    @Setter
    private MailMapper mailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class,
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public void writeMail(String recipient, MailDto mail, List<MailAttachDto> attachments) {
        // 메일 정보 저장
        mail.setRecipient(recipient); // 수신자 추가
        mailMapper.insertMail(mail);

        // 첨부파일이 존재하는 경우에만 처리
        if (attachments != null && !attachments.isEmpty()) {
            for (MailAttachDto attach : attachments) {
                attach.setEmailNo(mail.getEmailNo()); // 메일 번호 설정
                mailMapper.insertMailAttach(attach);
            }
        }
    }

    @Override
    public Map<String, Integer> getMailCountByStatus(String empEmail) {return mailMapper.countMailsByStatus(empEmail);}

    @Override
    public int getMailCount(String empEmail, String type, String searchText) {
        return mailMapper.countMails(empEmail, type, searchText);
    }
    @Override
    public List<MailDto> findMailByRange(int start, int count, String empEmail, String type, String searchText) {
        return mailMapper.selectMailByRange(start, count, empEmail, type, searchText);
    }
    @Override
    public MailDto findMailByMailNo(int emailNo, String empEmail) {
        MailDto mail = mailMapper.selectMailByMailNo(emailNo, empEmail);
        return mail;
    }

    @Override
    public void updateFromEmails(List<Long> fromEmailNos, String type) {
        // 이메일 삭제 로직
        mailMapper.updateFromEmails(fromEmailNos, type);
    }
    @Override
    public void updateToEmails(List<Long> toEmailNos, String type) {
        // 이메일 삭제 로직
        mailMapper.updateToEmails(toEmailNos, type);
    }

    @Override
    public MailDto findDetailMailByMailNo(int emailNo, String empEmail, String type) {
        // 첫 번째 메퍼 호출
        MailDto mail = mailMapper.selecDetailMailByMailNo(emailNo, empEmail, type);
        // 결과가 null인 경우 다른 메퍼 호출
        if (mail == null) {
            mail = mailMapper.selecDetailMailBySameMailNo(emailNo, empEmail, type);
        }
        return mail;
    }

    @Override
    public MailAttachDto findMailAttachByAttachNo(int attachNo) {
        MailAttachDto attach = mailMapper.selectMailAttachByAttachNo(attachNo);
        return attach;
    }

    @Override
    public PositionDto getPositionNameByPositionNo(int positionNo) {
        PositionDto position = mailMapper.selectPositionNameByPositionNo(positionNo);
        return position;
    }
}
