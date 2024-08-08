package com.groupware.controller;

import com.groupware.dto.AttendanceDto;
import com.groupware.dto.DepartmentDto;
import com.groupware.dto.EmployeeDto;
import com.groupware.mapper.AttdMapper;
import com.groupware.service.AccountService;
import com.groupware.service.AttdService;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Controller
public class AttendanceController {

    @Setter(onMethod_ = {@Autowired})
    private AttdService attdService;

    @Setter(onMethod_ = {@Autowired})
    private AccountService accountService;

    @Setter(onMethod_ = {@Autowired})
    private AttdMapper attdMapper;

    @GetMapping(path = "/modules/attendance-check-in")
    public String showAttendanceCheckIn(HttpSession session, Model model) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        return "/modules/attendance-check-in";
    }

    @PostMapping(path = "/modules/attendance-check-in", produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String AttendanceCheckIn(@RequestBody AttendanceDto attendance) throws ParseException {

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        Date attdIn = dateTimeFormat.parse(attendance.getAttdInStr());
        Date attdDate = dateFormat.parse(attendance.getAttdDateStr());

        // UTC 시간을 로컬 시간대로 변환
        TimeZone localTimeZone = TimeZone.getDefault();
        SimpleDateFormat localDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        localDateTimeFormat.setTimeZone(localTimeZone);

        String localAttdInStr = localDateTimeFormat.format(attdIn);
        String localAttdDateStr = dateFormat.format(attdDate);

        Date localAttdIn = localDateTimeFormat.parse(localAttdInStr);
        Date localAttdDate = dateFormat.parse(localAttdDateStr);

        attendance.setAttdIn(localAttdIn);
        attendance.setAttdDate(localAttdDate);

        attdService.updateAttendanceCheckIn(attendance);

        return "success";
    }

    @GetMapping(path = "/modules/attendance-check-out")
    public String showAttendanceCheckOut(HttpSession session, Model model) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        return "/modules/attendance-check-out";
    }

    @PostMapping(path = "/modules/attendance-check-out", produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String AttendanceCheckOut(@RequestBody AttendanceDto attendance) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        Date attdOut = dateTimeFormat.parse(attendance.getAttdOutStr()); // 수정된 부분
        Date attdDate = dateFormat.parse(attendance.getAttdDateStr());

        // UTC 시간을 로컬 시간대로 변환
        TimeZone localTimeZone = TimeZone.getDefault();
        SimpleDateFormat localDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        localDateTimeFormat.setTimeZone(localTimeZone);

        String localAttdOutStr = localDateTimeFormat.format(attdOut);
        String localAttdDateStr = dateFormat.format(attdDate);

        Date localAttdOut = localDateTimeFormat.parse(localAttdOutStr);
        Date localAttdDate = dateFormat.parse(localAttdDateStr);

        attendance.setAttdOut(localAttdOut);
        attendance.setAttdDate(localAttdDate);

        attdService.updateAttendanceCheckOut(attendance);

        return "success";
    }

    @GetMapping(path = "/pages/attendance/attendance-list")
    public String showAttendanceList(AttendanceDto attendance, HttpSession session, Model model) {
        //세션에 등록된 로그인 정보 조회
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        //로그인 정보의 deptNo으로 부서별 금일 근태 리스트 불러오기
        List<AttendanceDto> deptAttendanceToday = attdService.findAttendanceByDeptNoToday(loginUser.getDeptNo());
        model.addAttribute("deptAttendanceToday", deptAttendanceToday);

        List<DepartmentDto> allDepartment = attdService.findAllDepartment();
        model.addAttribute("allDepartment", allDepartment);


        return "/pages/attendance/attendance-list";
    }

    @PostMapping(path = "/pages/attendance/attendance-list", produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String requestAttendanceModify(@RequestBody AttendanceDto attendance) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//
        Date attdDate = dateFormat.parse(attendance.getAttdDateStr());
//
//        TimeZone localTimeZone = TimeZone.getDefault();
//        SimpleDateFormat localDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        localDateTimeFormat.setTimeZone(localTimeZone);
//
//        String localAttdDateStr = dateFormat.format(attdDate);
//
//        Date localAttdDate = dateFormat.parse(localAttdDateStr);
//
//        attendance.setAttdDate(localAttdDate);

        attendance.setAttdDate(attdDate);

        attdService.updateAttendanceToRequestModify(attendance);

        return "success";
    }

    @GetMapping(path = "/pages/attendance/attendance-edit")
    public String showAttendanceEdit(AttendanceDto attendance, HttpSession session, Model model) {
        //세션에 등록된 로그인 정보 조회
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        List<AttendanceDto> allAttendance = attdService.findAttendanceRequestModify();
        model.addAttribute("allAttendance", allAttendance);

        return "/pages/attendance/attendance-edit";
    }

    @PostMapping(path = "/pages/attendance/attendance-edit", produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String editAttendance(@RequestBody AttendanceDto attendance) throws ParseException {

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        Date attdOut = dateTimeFormat.parse(attendance.getAttdOutStr());
        Date attdIn = dateTimeFormat.parse(attendance.getAttdInStr());
        Date attdDate = dateFormat.parse(attendance.getAttdDateStr());

        // UTC 시간을 로컬 시간대로 변환
        TimeZone localTimeZone = TimeZone.getDefault();
        SimpleDateFormat localDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        localDateTimeFormat.setTimeZone(localTimeZone);

        String localAttdOutStr = localDateTimeFormat.format(attdOut);
        String localAttdInStr = localDateTimeFormat.format(attdIn);
        String localAttdDateStr = dateFormat.format(attdDate);

        Date localAttdOut = localDateTimeFormat.parse(localAttdOutStr);
        Date localAttdIn = localDateTimeFormat.parse(localAttdInStr);
        Date localAttdDate = dateFormat.parse(localAttdDateStr);

        attendance.setAttdIn(localAttdIn);
        attendance.setAttdOut(localAttdOut);
        attendance.setAttdDate(localAttdDate);

        attdService.updateAttendance(attendance);

        return "success";
    }

    @RequestMapping(path = {"/pages/attendance/attendance-test"})
    public String BlankPage(HttpSession session, Model model) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");


        AttendanceDto todayAttendance = attdService.findAttendanceByEmpIdAndCurDate(loginUser.getEmpId());

        model.addAttribute("todayAttendance", todayAttendance);

        AttendanceDto attendance = attdService.findAttendanceByEmpIdAndCurDate(loginUser.getEmpId());

        model.addAttribute("attendance", attendance);

        return "/pages/attendance/attendance-test";
    }

    @GetMapping(path = "/search/attendance")
    @ResponseBody
    public List<AttendanceDto> searchAttendance(@RequestParam("deptNo") Integer deptNo, @RequestParam("empName") String empName) {

        List<AttendanceDto> deptAttendanceToday = attdService.findAttendanceByEmpNameAndDeptNo(deptNo, empName);

        return attdService.findAttendanceByEmpNameAndDeptNo(deptNo, empName);

    }

    @GetMapping(path = "/modules/attendacne-button")
    public String showAttendanceButton() {
        return "/modules/attendance-button";
    }


    @GetMapping(path = "/pages/attendance/alert")
    public String alert() {
        return "/pages/attendance/alert";
    }

    @GetMapping(path = "/modules/organization")
    public String showOrganization(Model model) {

        List<EmployeeDto> allEmployee = attdMapper.findAllEmployeeInfo();
        model.addAttribute("allEmployee", allEmployee);

        return "/modules/organization";
    }
    @GetMapping(path = "/modules/organization2")
    public String showOrganization2(Model model) {

        List<EmployeeDto> allEmployee = attdMapper.findAllEmployeeInfo();
        model.addAttribute("allEmployee", allEmployee);

        return "/modules/organization2";
    }
}
