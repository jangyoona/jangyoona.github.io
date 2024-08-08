package com.groupware.scheduler;

import com.groupware.dto.MailDto;
import com.groupware.mapper.MailMapper;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MailScheduler {

    @Setter(onMethod_ = {@Autowired})
    private MailMapper mailMapper;

    @Scheduled(cron = "0 0 12 * * ?") // 매일 정오에 실행
    public void deleteExpiredMails() {
        List<MailDto> emailToList = mailMapper.getToDeletedEmailList();
        List<MailDto> emailFromList = mailMapper.getFromDeletedEmailList();
        LocalDateTime now = LocalDateTime.now();

        List<Long> fromEmailNosToUpdate = new ArrayList<>();
        List<Long> toEmailNosToUpdate = new ArrayList<>();

        for (MailDto mail : emailToList) {
            if (mail.getEmailToDeleteDate().isBefore(now.minusDays(3))) {
                toEmailNosToUpdate.add((long) mail.getEmailNo());
            }
        }

        for (MailDto mail : emailFromList) {
            if (mail.getEmailFromDeleteDate().isBefore(now.minusDays(3))) {
                fromEmailNosToUpdate.add((long) mail.getEmailNo());
            }
        }

        // 상태 업데이트 메서드 호출
        if (!fromEmailNosToUpdate.isEmpty()) {
            mailMapper.updateFromEmails(fromEmailNosToUpdate, "real-delete");
        }

        if (!toEmailNosToUpdate.isEmpty()) {
            mailMapper.updateToEmails(toEmailNosToUpdate, "real-delete");
        }

    }
    @PostConstruct
    public void init() {
        deleteExpiredMails(); // 서버 시작 시 초기화 작업 실행
    }
}
