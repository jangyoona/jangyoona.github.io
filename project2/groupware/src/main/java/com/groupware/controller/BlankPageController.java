package com.groupware.controller;

import com.groupware.dto.*;
import com.groupware.mapper.MinigameMapper;
import com.groupware.service.AttdService;
import com.groupware.service.CalendarService;
import com.groupware.service.MailService;
import com.groupware.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class BlankPageController {

    @Setter(onMethod_ = {@Autowired})
    private AttdService attdService;

    @Setter(onMethod_ = {@Autowired})
    private CalendarService calendarService;

    @Setter(onMethod_ = {@Autowired})
    private MailService mailService;

    @Setter(onMethod_ = {@Autowired})
    private MinigameMapper minigameMapper;

    @Setter(onMethod_ = {@Autowired})
    private ProjectService projectService;

    @RequestMapping(path = { "/", "/home" })
    public String BlankPage(HttpSession session, Model model) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인 유저 정보가 없으면 /account/login 페이지로 리다이렉트
            return "redirect:/account/login";
        }

        model.addAttribute("loginUser", loginUser);

        List<ProjectDto> projects = null;
        List<ProjectUserScheduleDto> projectUserTodos = null;

        if(loginUser != null) {
            // empId로 개인 캘린더 조회
            CalendarDto personalCalendar = calendarService.findCalendarByEmpId(loginUser.getEmpId(), 1); // calendarNo는 예시로 1을 사용

            // Project 일정 조회
            projects = projectService.findAllProjectsByDeptNo(loginUser.getDeptNo());

            // Project 관련 개인 Todo 일정 조회
            projectUserTodos = projectService.findProjectUserSchedulesByEmpId(loginUser.getEmpId());
            // 9시간 더해주는 로직
            for (ProjectUserScheduleDto projectUserSchedule : projectUserTodos) {
                projectUserSchedule.setUserScheduleStartDate(addHours(projectUserSchedule.getUserScheduleStartDate(), 9));
                projectUserSchedule.setUserScheduleEndDate(addHours(projectUserSchedule.getUserScheduleEndDate(), 9));
            }

        if (personalCalendar == null) {
            personalCalendar = new CalendarDto();
            personalCalendar.setEvents(new ArrayList<>()); // 빈 이벤트 리스트 설정
        }

        // 모델에 개인 캘린더 정보 추가
        model.addAttribute("calendar", personalCalendar);
        }
        //근태 정보
        AttendanceDto todayAttendance = attdService.findAttendanceByEmpIdAndCurDate(loginUser.getEmpId());

        model.addAttribute("todayAttendance", todayAttendance);

        AttendanceDto attendance = attdService.findAttendanceByEmpIdAndCurDate(loginUser.getEmpId());

        model.addAttribute("attendance", attendance);

        MinigameDto minigameInfo = minigameMapper.findMinigameInfoByEmpId(loginUser.getEmpId());
        model.addAttribute("minigameInfo", minigameInfo);

        List<MinigameDto> allMinigameInfo = minigameMapper.findAllMinigameInfo();
        model.addAttribute("allMinigameInfo", allMinigameInfo);

        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();
        model.addAttribute("currentHour", currentHour);

        // Project 일정
        model.addAttribute("projects", projects);
        // Project 관련 개인 Todo list
        model.addAttribute("projectUserTodos", projectUserTodos);

        return "blank";
    }

    public static Timestamp addHours(Timestamp timestamp, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.HOUR_OF_DAY, hours); // 양수를 사용하여 시간을 더함
        return new Timestamp(calendar.getTimeInMillis());
    }

}
