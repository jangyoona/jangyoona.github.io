package com.groupware.controller;

import com.groupware.common.Util;
import com.groupware.dto.*;
import com.groupware.service.AttdService;
import com.groupware.service.MailService;
import com.groupware.service.NotificationService;
import com.groupware.ui.MailPager;
import com.groupware.view.MailDownloadView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.File;
import java.util.*;

@Controller
@RequestMapping(path = {"/mailbox"})
public class MailController {

    @Setter(onMethod_ = {@Autowired})
    private MailService mailService;

    @Setter(onMethod_ = {@Autowired})
    private AttdService attdService;

    @Setter(onMethod_ = {@Autowired})
    private NotificationService notificationService;

    // 모든 /mailbox URL에 대해 총 메일 수를 모델에 추가
    @ModelAttribute
    public void addMailCount(Model model, HttpSession session) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        // 상태별 메일 수 가져오기
        Map<String, Integer> mailCountByStatus = mailService.getMailCountByStatus(employee.getEmpEmail());
        // 로그인 유저 직급 불러오기
        PositionDto employeePosition = mailService.getPositionNameByPositionNo(employee.getPositionNo());
        // 회원의 근태 관련 데이터 불러오기
        AttendanceDto employeeAttd = attdService.findAttendanceByEmpIdAndCurDate(employee.getEmpId());


        // 모델에 추가
        model.addAttribute("mailCountByStatus", mailCountByStatus);
        model.addAttribute("employeePosition", employeePosition);
        model.addAttribute("employeeAttd", employeeAttd);
    }
    @GetMapping(path = {"/all"})
    public  String all (){
        return "mailbox/all";
    }


    @GetMapping(path = { "/{type}-list" })
    public String listRange(@PathVariable String type,
                            @RequestParam(name="pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(name="searchText", required = false) String searchText,
                            HttpServletRequest req, Model model, HttpSession session) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        int pageSize = 10;
        int pagerSize = 3;
        if (searchText != null && searchText.isEmpty()) {
            searchText = null;
        }
        int dataCount = mailService.getMailCount(employee.getEmpEmail(), type, searchText);

        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1) ;
        MailPager pager = new MailPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);

        List<MailDto> mails =  mailService.findMailByRange(start, pageSize, employee.getEmpEmail(), type, searchText);



        model.addAttribute("mailList", mails);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("currentType", type);
        // HTML 템플릿 반환
        return "/mailbox/mail-list";

    }

    @GetMapping(path = { "/download" })
    public View downloadWithQueryString(@RequestParam("attachno") int attachNo, Model model) {

        MailAttachDto mailAttach = mailService.findMailAttachByAttachNo(attachNo);

        model.addAttribute("attach", mailAttach); // View에서 사용하도록 데이터 전달

        // 다운로드 처리 -> 사용자 정의 View 사용
        return new MailDownloadView();

    }

    @GetMapping(path = { "/write" })
    public String writeForm(@RequestParam(value = "emails", required = false) String emails, Model model) {
        if (emails != null) {
            String[] emailArray = emails.split(","); // 콤마로 구분된 문자열을 배열로 변환
            model.addAttribute("emailArray", emailArray); // 모델에 이메일 배열 추가
        }
        return "/mailbox/write";
    }

    @PostMapping(path = { "/write" })
    public String write(@ModelAttribute("mail") MailDto mail,
                        @RequestParam(value = "attach[]", required = false) MultipartFile[] attach,
                        @RequestParam(value = "emailTo", required = false) String emailTo,
                        HttpServletRequest req,
                        HttpSession session
                        ) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        ArrayList<MailAttachDto> attachments = new ArrayList<>();
        // 이메일 주소를 ';'로 분리하여 배열로 변환
        String[] emailAddresses = emailTo != null ? emailTo.split(";\\s*") : new String[0];
        // 첨부파일 처리
        if (attach != null && attach.length > 0) { // 첨부파일이 존재할 경우
            try {
                String dir = req.getServletContext().getRealPath("/mail-attachments");

                for (MultipartFile file : attach) { // 각 파일에 대해 반복 처리
                    if (!file.isEmpty()) { // 파일이 비어있지 않은 경우
                        String emailUserFileName = file.getOriginalFilename();
                        String emailSavedFileName = Util.makeUniqueFileName(emailUserFileName);
                        file.transferTo(new File(dir, emailSavedFileName)); // 파일 저장

                        MailAttachDto attachment = new MailAttachDto();
                        attachment.setEmailUserFileName(emailUserFileName);
                        attachment.setEmailSavedFileName(emailSavedFileName);
                        attachments.add(attachment);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // 예외 발생 시 로그 출력
            }
        }

        // 각 이메일 주소에 대해 메일 전송 로직 추가
        for (String recipient : emailAddresses) {
            mailService.writeMail(recipient, mail, attachments); // 각 수신자에게 메일 전송

            // 알림 DTO 생성 및 저장
            NotificationDto notificationDto = new NotificationDto(recipient, employee.getEmpName() + " 님이 보내신 메일이 도착했습니다.", 1);
            notificationService.saveNotification(notificationDto); // 알림 저장
        }

        // 나머지 처리 로직
        return "redirect:/mailbox/all"; // 성공 시 리다이렉트
    }

    @GetMapping(path = { "/detail" })
    public String detail(@RequestParam(value="emailNo", required = false) Integer emailNo,
                         @RequestParam(value="emailFrom", required = false) String emailFrom,
                         @RequestParam(value="emailTo", required = false) String emailTo,
                         Model model, HttpSession session) {
        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        String empEmail = employee.getEmpEmail();

        if (emailNo == null) { // 요청 데이터의 값이 없는 경우
            return "redirect:all";
        }

        // 읽기 상태 업데이트
        updateFromReadStatus(emailNo, emailFrom, empEmail); // 발신자
        updateToReadStatus(emailNo, emailTo, empEmail); // 수신자

        MailDto mail = mailService.findMailByMailNo(emailNo, empEmail);
        model.addAttribute("mail", mail);

        return "mailbox/detail";
    }

    private void updateToReadStatus(Integer emailNo, String emailAddress, String empEmail) {
        if (emailAddress != null && emailAddress.equals(empEmail)) {
            mailService.updateToEmails(Collections.singletonList(emailNo.longValue()), "read");
        }
    }
    private void updateFromReadStatus(Integer emailNo, String emailAddress, String empEmail) {
        if (emailAddress != null && emailAddress.equals(empEmail)) {
            mailService.updateFromEmails(Collections.singletonList(emailNo.longValue()), "read");
        }
    }


    @PostMapping("/{type}-mails")
    public ResponseEntity<String> handleMails(@PathVariable String type, @RequestBody MailListDto mailListDto, HttpSession session) {
        // 세션에서 로그인 사용자 이메일 가져오기
        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        String empEmail = employee.getEmpEmail();

        List<Long> fromEmailNos = new ArrayList<>(); // 발신자 이메일과 일치하는 이메일 번호 리스트
        List<Long> toEmailNos = new ArrayList<>();   // 수신자 이메일과 일치하는 이메일 번호 리스트

        // MailListDto에서 이메일 리스트 가져오기
        List<Mail> mails = mailListDto.getMails();

        // 이메일 리스트를 순회하며 분리
        for (Mail mail : mails) {
            if (mail.getEmailFrom().equals(empEmail)) {
                fromEmailNos.add(mail.getEmailNo());
                mailService.updateFromEmails(fromEmailNos, type);
            }
            if (mail.getEmailTo().equals(empEmail)) {
                toEmailNos.add(mail.getEmailNo());
                mailService.updateToEmails(toEmailNos, type);
            }

        }
        return ResponseEntity.ok("이메일 처리 완료");
    }

    @PostMapping("/{type}-detail")
    public ResponseEntity<Map<String, Object>> handleDetail(
            @PathVariable String type,
            @RequestBody MailListDto mailListDto,
            HttpSession session) {

        // 세션에서 로그인 사용자 이메일 가져오기
        EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
        String empEmail = employee.getEmpEmail();
        // 요청 본문에서 emailNo를 가져옴
        int emailNo = mailListDto.getEmailNo();
        // 이메일 리스트를 순회하며 분리
        MailDto mail = mailService.findDetailMailByMailNo(emailNo, empEmail, type);

        Map<String, Object> response = new HashMap<>();
        if (mail != null) {
            response.put("newEmailNo", mail.getEmailNo());
            response.put("emailFrom", mail.getEmailFrom());
            response.put("emailTo", mail.getEmailTo());
        } else {
            response.put("error", "메일을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(response);
    }

}
