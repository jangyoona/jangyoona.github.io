package com.groupware.controller;

import com.groupware.common.Util;
import com.groupware.dto.*;
import com.groupware.service.AccountService;
import com.groupware.service.AttdService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.Position;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class AccountController {

    @Setter(onMethod_ = { @Autowired })
    private AccountService accountService;

    @Setter(onMethod_ = { @Autowired })
    private AttdService attdService;

    @Setter(onMethod_ = { @Autowired })
    private JavaMailSender mailSender;


    @GetMapping("/account/login")
    public String loginForm(HttpServletRequest request, Model model){
        String userName = "";
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("saveId")) {
                    userName = cookie.getValue();
                }
            }
        }
        model.addAttribute("saveId", userName);
        return "account/login";
    }

    @PostMapping("/account/login")
    public String login(int empId, String empPasswd, @RequestParam(required = false) String saveId, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // saveId => null OR true
        EmployeeDto loginUser = accountService.signInUser(empId, empPasswd);

        if (loginUser != null) {
            session.setAttribute("loginUser", loginUser);

            // Check and delete existing cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("saveId")) {
                        // 기존 쿠키가 있으면 삭제
                        Cookie deleteCookie = new Cookie("saveId", "");
                        deleteCookie.setMaxAge(0);
                        deleteCookie.setPath("/");
                        response.addCookie(deleteCookie);
                        break;
                    }
                }
            }

            // 아이디 저장 체크하면 쿠키 새로 저장 or 미 체크시 새로 저장안하고 위에서 삭제만 됨.
            if (saveId != null) {
                Cookie newCookie = new Cookie("saveId", String.valueOf(empId));
                newCookie.setMaxAge(60 * 60 * 24 * 30); // 30일 저장
                newCookie.setPath("/");
                response.addCookie(newCookie);
            }

            return "redirect:/home";
        } else {
            return "redirect:/account/login?loginfail=true";
        }
    }


    @GetMapping("/account/logout")
    public String logout() {
//        session.removeAttribute("loginUser"); LoginAttempt에서 처리함.
        return "redirect:/home";
    }


    @GetMapping("/account/user-check")
    public String userCheckForm(){

        return "account/user-check";
    }
    @GetMapping("/account/id-check")
    @ResponseBody
    public String userCheckForm(Integer empId){
        System.out.println(empId);
        boolean dup = accountService.getCheckUserId(empId);
        return String.valueOf(dup);

    }


    // 비밀번호 재설정 전 이메일 인증 -
    @GetMapping("/account/email-message")
    @ResponseBody
    public String emailMessageForm(Integer empId, HttpServletRequest req) {

        String from = "olozg@naver.com";
        String to = accountService.getUserEmail(empId);
        String title = "비밀번호 재설정";
        int key = (int)(Math.random() * 8999) + 1000;
        System.out.println(key);

        boolean success = true;
        ServletContext sc = req.getServletContext();
        sc.setAttribute(to, key);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom(from);
            messageHelper.setTo(new String[]{to});
            messageHelper.setSubject(title);
//            message.setContent(String.format("<html><body><a href='http://localhost:8081/account/reset-password?empId=%d&key=%d'>비밀번호 재설정 인증 메일</a><br><br>위 링크로 접속하여 비밀번호를 재설정 해주세요.</body></html>", empId, key),
            message.setContent(String.format("<html><body><h2>비밀번호 재설정 인증 메일</h2><br><br> 인증번호: %d </body></html>", key),
                    "text/html; charset=UTF-8");

            mailSender.send(message);

        } catch (Exception ex) {
            ex.printStackTrace();
            success = false;
        }
            accountService.insertEmailKey(String.valueOf(empId),key);

        return "success";

    }

    @GetMapping("/account/email-check")
    public String emailDupForm() {

        return "account/email-check";
    }

    @GetMapping("/account/email-checkAuthCode")
    @ResponseBody
    public String emailDup(Integer empId, Integer number) {

        if(empId == null) {
            return "null";
        }
        if(number < 1000){
            return "length-fail";
        }

        int sentCode = accountService.getEmailAuthCode(empId);
        return number == sentCode ? "true" : "false";
    }

    @GetMapping("/account/reset-password")
    public String resetPasswordForm() {
        return "account/reset-password";
    }


    @PostMapping("/account/reset-password")
    public String resetPassword(String empId, String passwd, String confirmPasswd){

        boolean dup = accountService.getCheckUserId(Integer.parseInt(empId));
        if(dup){
            if(passwd.equals(confirmPasswd)) {
                accountService.editUserPasswd(empId, passwd);
            } else {
                return "redirect:/account/reset-password?passwdfail=true";
            }
        }
        return "redirect:/account/login";
    }


    @GetMapping("/account/register")
    public String registerForm(Model model) {
        String latestEmpId = accountService.getLatestEmpId();

            // 오늘 날짜 가져오기
            LocalDate today = LocalDate.now();
            String year = String.format("%02d", today.getYear() % 100); // Get last 2 digits of the year
            String month = String.format("%02d", today.getMonthValue()); // Ensure month is 2 digits
            String day = String.format("%02d", today.getDayOfMonth()); // Ensure day is 2 digits

            // 날짜 접두사 생성
            String datePrefix = year + month;

            // 최신 사번의 뒤 4자리 숫자를 +1하여 새로운 사번 생성
            int latestNumber = Integer.parseInt(latestEmpId.substring(4, 8)); // 뒤 4자리 숫자 추출
            String newNumber = String.format("%04d", latestNumber + 1); // 새로운 숫자 생성, 4자리로 보장

            // 새로운 사번 생성
            String newEmpId = datePrefix + newNumber;
            model.addAttribute("newEmpId", newEmpId);

            // 부서와 직위 불러오기
        List<DepartmentDto> department = attdService.findAllDepartment();
        model.addAttribute("department", department);

        List<PositionDto> position = attdService.findAllPosition();
        model.addAttribute("position",position);

        return "account/register";

    }

    @PostMapping("/account/register")
    public String register(EmployeeDto employee,
                           EmployeeDetailDto employeeDetail,
                           AnnualDto annual,
                           HttpServletRequest req) {

        List<EmployeeAttachDto> attachments = new ArrayList<>();

        if (employee.getEmpFiles() != null && employee.getEmpFiles().length > 0) { // 첨부파일이 존재할 경우
            try {
                String dir = req.getServletContext().getRealPath("/employee-attachments");

                for (MultipartFile file : employee.getEmpFiles()) { // 각 파일에 대해 반복 처리
                    if (!file.isEmpty()) { // 파일이 비어있지 않은 경우
                        String userFileName = file.getOriginalFilename();
                        String savedFileName = Util.makeUniqueFileName(userFileName);
                        file.transferTo(new File(dir, savedFileName)); // 파일 저장

                        System.out.println(dir);
                        System.out.println(userFileName);
                        System.out.println(savedFileName);
                        System.out.println("Saving file to: " + new File(dir, savedFileName).getAbsolutePath());

                        EmployeeAttachDto attachment = new EmployeeAttachDto();
                        attachment.setUserFilename(userFileName);
                        attachment.setSavedFilename(savedFileName);
                        attachments.add(attachment);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // 예외 발생 시 로그 출력
            }
        }
        // 근태 기록 삽입때문에 넣었음
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String attdDate = dateFormat.format(now);

        employee.setEmployeeAttach(attachments);
        accountService.insertUser(employee, attachments);
        accountService.insertUserDetail(employeeDetail);
        accountService.insertAnnual(annual);
        attdService.insertAttendance(employee.getEmpId(), attdDate);

        return "redirect:/home";
    }



    @GetMapping("/account/modify")
    public String showModify(Model model, @RequestParam("empId") Integer empId) {
        // 부서와 직위 불러오기
        List<DepartmentDto> department = attdService.findAllDepartment();
        model.addAttribute("department", department);

        List<PositionDto> position = attdService.findAllPosition();
        model.addAttribute("position",position);

        List<EmployeeDetailDto> EmployeeRequestModify = accountService.findEmployeeRequestModify();
        model.addAttribute("EmployeeRequestModify", EmployeeRequestModify);

        if (empId != null) {
            EmployeeDto employee = accountService.findEmployeeByEmpId(empId);
            model.addAttribute("employee", employee);
        } else {
            model.addAttribute("employee", new EmployeeDto());
        }

        return "account/modify";

    }

    @PostMapping("/account/modify")
    public String modify(EmployeeDto employee, EmployeeDetailDto employeeDetail) {

        accountService.updateEmployeeByEmpId(employee);
        accountService.updateEmployeeDetailByEmpId(employeeDetail);

        return "redirect:/account/modify?empId=24071241";
    }

    @GetMapping("/account/getEmployee")
    @ResponseBody
    public EmployeeDto getEmployee(@RequestParam("empId") Integer empId) {
        return accountService.findEmployeeByEmpId(empId);
    }

    @PostMapping(path = "/account/requestModify", produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String modifyRequest(@RequestParam("empId") String empIdStr, @RequestParam("modifyDetail") String modifyDetail) {

        int empId = Integer.parseInt(empIdStr);
        System.out.println(empId);
        System.out.println(modifyDetail);

        accountService.updateEmployeeInfoToRequestModify(empId, modifyDetail);

        return "success";
    }

}
