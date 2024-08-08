package com.groupware.service;

import com.groupware.dto.AttendanceDto;
import com.groupware.dto.DepartmentDto;
import com.groupware.dto.PositionDto;
import com.groupware.mapper.AttdMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.Position;
import java.util.Date;
import java.util.List;

@Service
public class AttdServiceImpl implements AttdService{

    @Setter
    private AttdMapper attdMapper;

    @Autowired
    public AttdServiceImpl(AttdMapper attdMapper) {
        this.attdMapper = attdMapper;
    }

    @Override
    public void updateAttendanceCheckIn(AttendanceDto attendance) {
        attdMapper.updateAttendanceCheckIn(attendance);
    }

    @Override
    public void updateAttendanceCheckOut(AttendanceDto attendance) {
        attdMapper.updateAttendanceCheckOut(attendance);}

    @Override
    public void updateAttendance(AttendanceDto attendance){
        attdMapper.updateAttendance(attendance);}

    @Override
    public void updateAttendanceToRequestModify(AttendanceDto attendance) {
        attdMapper.updateAttendanceToRequestModify(attendance);
    }

    @Override
    public List<AttendanceDto> findAllAttendance() {
        return attdMapper.findAllAttendance();
    }

    @Override
    public List<AttendanceDto> findAttendanceByEmpId(Integer empId) {
        return attdMapper.findAttendanceByEmpId(empId);
    }
    @Override
    public List<AttendanceDto> findAttendanceByDeptNo(Integer deptNo) {
        return attdMapper.findAttendanceByDeptNo(deptNo);
    }
    @Override
    public List<AttendanceDto> findAttendanceByDeptNoToday(Integer deptNo) {
        return attdMapper.findAttendanceByDeptNoToday(deptNo);
    }

    @Override
    public List<AttendanceDto> findAttendanceByAttdDate(Date attdDate) {
        return attdMapper.findAttendanceByAttdDate(attdDate);
    }

    @Override
    public AttendanceDto findAttendanceByEmpIdAndCurDate(Integer empId) {
        return attdMapper.findAttendanceByEmpIdAndCurDate(empId);
    }
    @Override
    public AttendanceDto findAttendanceByEmpNameAndAttdDate(String empName, Date attdDate) {
        return attdMapper.findAttendanceByEmpNameAndAttdDate(empName, attdDate);
    }
    @Override
    public List<AttendanceDto> findAttendanceRequestModify() {
        return attdMapper.findAttendanceRequestModify();
    }

    @Override
    public List<DepartmentDto> findAllDepartment() {
        return attdMapper.findAllDepartment();
    }

    @Override
    public List<PositionDto> findAllPosition() {
        return attdMapper.findAllPosition();
    }
    //휴가내역조회 (이동예정)
    @Override
    public List<AttendanceDto> findAttendanceLeavesByEmpId(Integer empId) {
        return attdMapper.findAttendanceLeavesByEmpId(empId);
    }

    @Override
    public List<AttendanceDto> findAttendanceByEmpNameAndDeptNo(Integer deptNo, String empName) {
        return attdMapper.findAttendanceByEmpNameAndDeptNo(deptNo, empName);
    }

    @Override
    public void insertAttendance(Integer empId, String attdDate) {
        attdMapper.insertAllAttendance(empId, attdDate);
    }

}
