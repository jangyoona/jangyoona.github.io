package com.groupware.service;

import com.groupware.common.Util;
import com.groupware.dto.*;
import com.groupware.mapper.AccountMapper;
import com.groupware.mapper.AttdMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountServiceImpl implements AccountService {

    @Setter
    private AccountMapper accountMapper;

    @Override
    public EmployeeDto signInUser(int empId, String empPasswd) {

        String hashedPasswd = Util.getHashedString(empPasswd, "SHA-256");
        Map<String, Object> params = new HashMap<>();
        params.put("empId", empId);
        params.put("empPasswd", hashedPasswd);

        EmployeeDto loginEmployee = accountMapper.findByEmpIdAndEmpPasswd(params);
        EmployeeDetailDto loginEmployeeDetail = accountMapper.findEmployeeDetailByIdAndEmpPasswd(empId);

        if(loginEmployee == null || loginEmployeeDetail == null) {
            return null;
        } else {
            loginEmployee.setEmployeeDetail(loginEmployeeDetail);
            return loginEmployee;
        }

    }

    @Override
    public boolean getCheckUserId(int empId) {
        int count = accountMapper.findByEmpId(empId);
        return count > 0;
    }


    @Override
    public String getUserEmail(int empId) {

        return accountMapper.findByEmpEmail(empId);
    }


    @Override
    public void insertEmailKey(String key, int code) {

        Map<String, Object> param = new HashMap<>();
        param.put("key", key);
        param.put("code", code);

        accountMapper.deleteTempColumnByEmpId(Integer.parseInt(key));
        accountMapper.insertEmailKey(param);
    };

    @Override
    public int getEmailAuthCode(int empId) {
        return accountMapper.findByTempValue(empId); // findById(empId)
    }


    @Override
    public void editUserPasswd(String empId, String empPasswd) {
//        boolean inputIdCheck = getCheckUserId(Integer.parseInt(empId)); 이거 ajax로 옮기셈
        String hashedPasswd = Util.getHashedString(empPasswd, "SHA-256");
        accountMapper.updateEmpPasswd(empId, hashedPasswd);
        accountMapper.deleteTempColumnByEmpId(Integer.parseInt(empId));
    }


    public void insertUser(EmployeeDto employee, List<EmployeeAttachDto> attachments) {
        String hashedPasswd = Util.getHashedString(employee.getEmpPasswd(), "SHA-256");
        employee.setEmpPasswd(hashedPasswd);

        accountMapper.insertEmp(employee);

        if (attachments != null && !attachments.isEmpty()) {
            for (EmployeeAttachDto attach : attachments) {
                attach.setEmpId(employee.getEmpId()); // FK 설정
                accountMapper.insertEmployeeAttach(attach);
            }
        }
    }

    public void insertUserDetail(EmployeeDetailDto employeeDetail) {

        accountMapper.insertEmpDetail(employeeDetail);
    }

    public String getLatestEmpId() {
        return accountMapper.getLatestEmpId();
    }

    public void insertAnnual(AnnualDto annual) {

        accountMapper.insertAnnual(annual);
    }

    public EmployeeDto findEmployeeByEmpId(Integer empId) {
        return accountMapper.findEmployeeByEmpId(empId);
    }

    public void updateEmployeeByEmpId(EmployeeDto employee) {
        accountMapper.updateEmployeeByEmpId(employee);
    }

    public void updateEmployeeDetailByEmpId(EmployeeDetailDto employeeDetail) {
        accountMapper.updateEmployeeDetailByEmpId(employeeDetail);
    }

    public void updateEmployeeInfoToRequestModify(Integer empId, String modifyDetail) {
        accountMapper.updateEmployeeInfoToRequestModify(empId, modifyDetail);
    }

    public List<EmployeeDetailDto> findEmployeeRequestModify() {
        return accountMapper.findEmployeeRequestModify();
    }


}
