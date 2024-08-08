package com.groupware.service;

import com.groupware.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AccountService {

    EmployeeDto signInUser(int empId, String empPasswd);

    boolean getCheckUserId(int empId);
    String getUserEmail(int empId);
    void insertEmailKey(String result, int key);
    int getEmailAuthCode(int empId);
//    void deleteEmailAuthCode(int key);
    void editUserPasswd(String empId, String empPasswd);


    void insertUser(EmployeeDto employee, List<EmployeeAttachDto> attachments);

    void insertUserDetail(EmployeeDetailDto employeeDetail);

    String getLatestEmpId();

    void insertAnnual(AnnualDto annual);

    EmployeeDto findEmployeeByEmpId(Integer empId);

    void updateEmployeeByEmpId(EmployeeDto employee);

    void updateEmployeeDetailByEmpId(EmployeeDetailDto employeeDetail);

    void updateEmployeeInfoToRequestModify(Integer empId, String modifyDetail);

    List<EmployeeDetailDto> findEmployeeRequestModify();
}
