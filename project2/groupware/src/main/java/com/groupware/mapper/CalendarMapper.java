package com.groupware.mapper;

import com.groupware.dto.CalendarDto;
import com.groupware.dto.CalendarEventDto;
import com.groupware.dto.EmployeeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarMapper {

    /* 캘린더 삽입(최초에만 해당) */
    void insertCalendar(CalendarDto calendar);

    //이벤트 삽입
    void insertCalendarEvent(CalendarEventDto event);

    //이벤트 수정
    void updateCalendarEvent(CalendarEventDto event);

    //이벤트 삭제
    void deleteAllEventsByCalendarNo(@Param("calendarNo") Integer calendarNo);

    void deleteEventsByEventId(@Param("eventId") Integer empId);

    //개인 캘린더 조회
    CalendarDto findCalendarByEmpId(@Param("empId") Integer empId);

    //부서 캘린더 조회
    CalendarDto findCalendarByDeptNo(@Param("deptNo") Integer deptNo);

    // 해당 캘린더의 이벤트 조회
    List<CalendarEventDto> findCalendarEventsByCalendarNo(@Param("calendarNo") Integer calendarNo);

    void insertCalendarEventDefault(@Param("calendarNo") Integer calendarNo);

}
