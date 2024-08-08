package com.groupware.mapper;

import com.groupware.dto.AttendanceDto;
import com.groupware.dto.DepartmentDto;
import com.groupware.dto.EmployeeDto;
import com.groupware.dto.PositionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AttdMapper {

    void updateAttendanceCheckIn(AttendanceDto attendance);

    void updateAttendanceCheckOut(AttendanceDto attendance);

    void updateAttendance(AttendanceDto attendance);

    void updateAttendanceToRequestModify(AttendanceDto attendance);

    // 휴가 내역 조회 ( 다른곳으로 옮길 예정 )
    List<AttendanceDto> findAttendanceLeavesByEmpId(Integer empId);

    List<AttendanceDto> findAllAttendance();

    AttendanceDto findAttendanceByEmpNameAndAttdDate(String empName, Date attdDate);

    List<AttendanceDto> findAttendanceByDeptNoToday(Integer deptNo);

    List<AttendanceDto> findAttendanceByEmpId(Integer empId);

    List<AttendanceDto> findAttendanceByDeptNo(Integer deptNo);

    AttendanceDto findAttendanceByEmpIdAndCurDate(Integer empId);

    List<AttendanceDto> findAttendanceRequestModify();

    List<EmployeeDto> findAllEmployee();

    void insertAllAttendance(Integer empId, String attdDate);

    void insertAllAttendanceDayOff(Integer empId, String attdDate);

    List<AttendanceDto> findAttendanceByAttdDate(Date attdDate);

    List<DepartmentDto> findAllDepartment();

    List<PositionDto> findAllPosition();

    void updateAttendanceStatusToAbsence();

    void updateAttendanceStatusToAbnormal();

    List<Integer> findEmployeesWithAttendanceByToday(@Param("attdDate") String attdDate);

    List<EmployeeDto> findAllEmployeesExceptThoseWithAttendance(@Param("excludedEmpIds") List<Integer> excludedEmpIds);

    List<AttendanceDto> findAttendanceByEmpNameAndDeptNo(@Param("deptNo") Integer deptNo, @Param("empName") String empName);

    List<EmployeeDto> findAllEmployeeInfo();
}
