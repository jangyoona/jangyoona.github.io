package com.groupware.mapper;

import com.groupware.dto.AnnualDto;
import com.groupware.dto.EmployeeAttachDto;
import com.groupware.dto.EmployeeDetailDto;
import com.groupware.dto.EmployeeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper {

    EmployeeDto findByEmpIdAndEmpPasswd(Map idPw);
    EmployeeDetailDto findEmployeeDetailByIdAndEmpPasswd(int empId);

    int findByEmpId(int empId);
    String findByEmpEmail(int empId);
    void insertEmailKey(Map<String, Object> param);
    int findByTempValue(int empId);
    void deleteTempColumnByEmpId(int key);
    void updateEmpPasswd(String empId, String empPasswd);


    void insertEmp(EmployeeDto employee);

    void insertEmpDetail(EmployeeDetailDto employeeDetail);

    void insertAnnual(AnnualDto annual);

    String getLatestEmpId();

    EmployeeDto findEmployeeByEmpId(Integer empId);

    void updateEmployeeByEmpId(EmployeeDto employee);

    void updateEmployeeDetailByEmpId(EmployeeDetailDto employeeDetail);

    void updateEmployeeInfoToRequestModify(Integer empId, String modifyDetail);

    List<EmployeeDetailDto> findEmployeeRequestModify();

    void insertEmployeeAttach(EmployeeAttachDto employeeAttach);



}
