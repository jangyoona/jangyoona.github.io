package com.groupware.service;

import com.groupware.dto.CalendarDto;
import com.groupware.dto.CalendarEventDto;
import com.groupware.mapper.CalendarMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService {

    @Setter(onMethod_ = {@Autowired})
    private CalendarMapper calendarMapper;

    @Override
    public void insertCalendar(CalendarDto calendar){
        calendarMapper.insertCalendar(calendar);
    }

    @Override
    public void insertCalendarEventDefault(Integer calendarNo) {
        calendarMapper.insertCalendarEventDefault(calendarNo);
    }

    @Override
    public void insertCalendarEvent(CalendarEventDto event) {
        calendarMapper.insertCalendarEvent(event);
    }

    @Override
    public void updateCalendarEvent(CalendarEventDto event) {
        calendarMapper.updateCalendarEvent(event);
    }

    @Override
    public void deleteAllEventsByCalendarNo(Integer calendarNo) {
        calendarMapper.deleteAllEventsByCalendarNo(calendarNo);
    }

    @Override
    public void deleteEventsByEventId(Integer eventId) {
        calendarMapper.deleteEventsByEventId(eventId);
    }

    @Override
    public CalendarDto findCalendarByEmpId(Integer empId, Integer calendarNo) {
        CalendarDto personalCalendar = calendarMapper.findCalendarByEmpId(empId);
        if (personalCalendar != null) {
            List<CalendarEventDto> events = calendarMapper.findCalendarEventsByCalendarNo(personalCalendar.getCalendarNo());
            personalCalendar.setEvents(events);
        } else {
            personalCalendar = new CalendarDto();
            personalCalendar.setEvents(new ArrayList<>()); // 빈 이벤트 리스트 설정
        }
        return personalCalendar;
    }

    @Override
    public CalendarDto findCalendarByDeptNo(Integer deptNo, CalendarDto calendar) {
        CalendarDto deptCalendar = calendarMapper.findCalendarByDeptNo(deptNo);
        if (deptCalendar != null) {
            List<CalendarEventDto> events = calendarMapper.findCalendarEventsByCalendarNo(deptCalendar.getCalendarNo());
            deptCalendar.setEvents(events);
        } else {
            deptCalendar = new CalendarDto();
            deptCalendar.setEvents(new ArrayList<>()); // 빈 이벤트 리스트 설정
        }
        return deptCalendar;
    }

    @Override
    public List<CalendarEventDto> findCalendarEventsByCalendarNo(Integer calendarNo) {
        return calendarMapper.findCalendarEventsByCalendarNo(calendarNo);

    }

}
