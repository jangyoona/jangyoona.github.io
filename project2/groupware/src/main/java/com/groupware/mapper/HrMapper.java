package com.groupware.mapper;

import com.groupware.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface HrMapper {

    void insertAttendanceLeaves(@Param("empId") Integer empId, @Param("date") String date, @Param("detail") String detail);

    void updateAnnualCountPlusAllEmployee();

    void updateAnnualCountMinus(@Param("empId") Integer empId);

    List<EmployeeDto> findAllEmployee();

    List<EmployeeDto> findEmployeeByEmpName(String empName);

    Integer findAuunalCountByEmpId(Integer empId);

    List<ApprovalDto> findApprovalLeavesByActive();

    int selectEmployeesCount(@RequestParam("selectValue") int selectValue, @RequestParam("searchValue") String searchValue);

    List<EmployeeDto> selectEmployees(@RequestParam("start") int start, @RequestParam("selectValue") int selectValue, @RequestParam("searchValue") String searchValue);
}
