package com.groupware.service;

import com.groupware.dto.CalendarDto;
import com.groupware.dto.CalendarEventDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CalendarService {

    void insertCalendar(CalendarDto calendar);
    //이벤트 삽입
    void insertCalendarEvent(CalendarEventDto event);

    void insertCalendarEventDefault(Integer calendarNo);
    //이벤트 수정
    void updateCalendarEvent(CalendarEventDto event);

    //이벤트 삭제
    void deleteAllEventsByCalendarNo(Integer calendarNo);

    void deleteEventsByEventId(Integer eventId);

    //개인 캘린더 조회
    CalendarDto findCalendarByEmpId(Integer empId, Integer calendarNo);

    //부서 캘린더 조회
    CalendarDto findCalendarByDeptNo(Integer deptNo, CalendarDto calendar);

    List<CalendarEventDto> findCalendarEventsByCalendarNo(Integer calendarNo);
}
