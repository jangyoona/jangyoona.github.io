package com.groupware.scheduler;

import com.groupware.dto.EmployeeDto;
import com.groupware.mapper.AttdMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class AttdScheduler {

    @Setter(onMethod_ = {@Autowired})
    private AttdMapper attdMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void insertAllEmployeesAttendance() throws ParseException {

        Date now = new Date();

        // 9시간 더하기
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR_OF_DAY, 9);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        // 날짜를 yyyy-MM-dd 형식으로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String attdDate = dateFormat.format(cal.getTime());

        // 해당 날짜의 근태 기록 조회
        List<Integer> excludedEmpIds = attdMapper.findEmployeesWithAttendanceByToday(attdDate);

        // 근태 기록이 존재하지 않는 사원 조회
        List<EmployeeDto> employeesWithoutAttendance;
        if (excludedEmpIds.isEmpty()) {
            employeesWithoutAttendance = attdMapper.findAllEmployee();
        } else {
            employeesWithoutAttendance = attdMapper.findAllEmployeesExceptThoseWithAttendance(excludedEmpIds);
        }

        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            // 토요일 또는 일요일인 경우
            for (EmployeeDto employee : employeesWithoutAttendance) {
                attdMapper.insertAllAttendanceDayOff(employee.getEmpId(), attdDate);
            }
        } else {
            for (EmployeeDto employee : employeesWithoutAttendance) {
                attdMapper.insertAllAttendance(employee.getEmpId(), attdDate);
                }
            }
        }

    @Scheduled(cron = "0 59 23 * * ?")
    public void updateAttendanceAbnormal() {
        attdMapper.updateAttendanceStatusToAbsence();
        attdMapper.updateAttendanceStatusToAbnormal();
    }
}