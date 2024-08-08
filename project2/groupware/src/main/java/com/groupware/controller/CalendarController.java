package com.groupware.controller;

import com.groupware.dto.CalendarDto;
import com.groupware.dto.CalendarEventDto;
import com.groupware.dto.EmployeeDto;
import com.groupware.service.CalendarService;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CalendarController {

    @Setter(onMethod_ = {@Autowired})
    private CalendarService calendarService;

    @GetMapping("/modules/calendar")
    public String showCalendar(HttpSession session, Model model) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

        // empId로 개인 캘린더 조회
        CalendarDto personalCalendar = calendarService.findCalendarByEmpId(loginUser.getEmpId(), 1); // calendarNo는 예시로 1을 사용

        if (personalCalendar == null) {
            personalCalendar = new CalendarDto();
            personalCalendar.setEvents(new ArrayList<>()); // 빈 이벤트 리스트 설정
        }
        // 모델에 개인 캘린더 정보 추가
        model.addAttribute("calendar", personalCalendar);

        return "/modules/calendar";
    }

    //기존에 있던건 update, 새로운건 insert로 처리하려고했는데 진짜 하다가 너무 안되서 이렇게 바꿈
    @PostMapping("/calendar-save")
    @ResponseBody
    public String saveCalendarEvents(@RequestBody CalendarDto calendarDto) {
        Integer calendarNo = calendarDto.getCalendarNo();

        // 해당 캘린더의 기존 이벤트를 조회
        List<CalendarEventDto> existingEvents = calendarService.findCalendarEventsByCalendarNo(calendarNo);
        Map<Integer, CalendarEventDto> existingEventsMap = existingEvents.stream()
                .collect(Collectors.toMap(CalendarEventDto::getEventId, event -> event));

        // 새로운 이벤트를 삽입 또는 업데이트
        List<CalendarEventDto> newEvents = calendarDto.getEvents();
        Set<Integer> newEventIds = new HashSet<>(); // 새로운 이벤트 ID들을 저장할 집합
        for (CalendarEventDto newEvent : newEvents) {
            newEvent.setCalendarNo(calendarNo); // 캘린더 번호 설정
            if (newEvent.getEventId() != null && existingEventsMap.containsKey(newEvent.getEventId())) {
                // 기존 이벤트 업데이트
                calendarService.updateCalendarEvent(newEvent);
            } else {
                // 새로운 이벤트 삽입
                calendarService.insertCalendarEvent(newEvent);
            }
            if (newEvent.getEventId() != null) {
                newEventIds.add(newEvent.getEventId());
            }
        }

        // 새로운 이벤트 목록에 포함되지 않은 기존 이벤트를 삭제
        for (Integer existingEventId : existingEventsMap.keySet()) {
            if (!newEventIds.contains(existingEventId)) {
                calendarService.deleteEventsByEventId(existingEventId);
            }
        }

        return "success";
    }



    @PostMapping("/create-calendar")
    @ResponseBody
    public String insertCalendar(@RequestBody CalendarDto calendar) {

        calendarService.insertCalendar(calendar);

        return "success";
    }

}
