package com.groupware.service;

import com.groupware.dto.AttendanceDto;
import com.groupware.dto.DepartmentDto;
import com.groupware.dto.PositionDto;

import java.util.Date;
import java.util.List;

public interface AttdService {

    void updateAttendanceCheckIn(AttendanceDto attendance);

    void updateAttendanceCheckOut(AttendanceDto attendance);

    void updateAttendance(AttendanceDto attendance);

    void updateAttendanceToRequestModify(AttendanceDto attendance);

    List<AttendanceDto> findAllAttendance();

    List<AttendanceDto> findAttendanceByEmpId(Integer empId);

    List<AttendanceDto> findAttendanceByDeptNo(Integer deptNo);

    List<AttendanceDto> findAttendanceByDeptNoToday(Integer deptNo);

    List<AttendanceDto> findAttendanceByAttdDate(Date attdDate);

    AttendanceDto findAttendanceByEmpIdAndCurDate(Integer empId);

    AttendanceDto findAttendanceByEmpNameAndAttdDate(String empName, Date attdDate);

    List<AttendanceDto> findAttendanceRequestModify();

    //휴가내역조회 (이동예정)
    List<AttendanceDto> findAttendanceLeavesByEmpId(Integer empId);

    // 부서와 직위 가져오기 위함
    List<DepartmentDto> findAllDepartment();

    public List<PositionDto> findAllPosition();

    List<AttendanceDto> findAttendanceByEmpNameAndDeptNo(Integer deptNo, String empName);

    void insertAttendance(Integer empId, String attdDate);
}
