package com.groupware.service;

import com.groupware.dto.AttendanceDto;
import com.groupware.dto.EmployeeDetailDto;
import com.groupware.dto.EmployeeDto;
import com.groupware.dto.VacationRegistration;
import org.jsoup.nodes.Document;

import java.util.List;

public interface HrService {

//    void insertAttendanceLeaves(AttendanceDto attendance);

    List<EmployeeDto> findAllEmployee();

    List<EmployeeDto> findEmployeeByEmpName(String empName);

    void insertAttendanceLeavesAuto(VacationRegistration vacationRegistration);

    List<VacationRegistration> findAndParseApprovalLeaves();

    String extractValue(Document doc, String tag);

    int getEmployeesCount(int selectValue, String searchValue);

    List<EmployeeDto> findEmployees(int start, int selectValue, String searchValue);
}
