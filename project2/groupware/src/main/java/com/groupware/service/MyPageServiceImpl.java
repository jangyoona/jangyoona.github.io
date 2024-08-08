package com.groupware.service;

import com.groupware.dto.*;
import com.groupware.loginAttempt.LoginAttemptDto;
import com.groupware.mapper.HrMapper;
import com.groupware.mapper.LoginAttemptMapper;
import com.groupware.mapper.MyPageMapper;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPageServiceImpl implements MyPageService {

    @Setter
    private MyPageMapper myPageMapper;

    @Setter
    private LoginAttemptMapper loginAttemptMapper;

    @Setter
    private HrMapper hrMapper;

    @Override
    public List<UserScheduleDto> getUserSchedule(int empId, String sortKeyword) {
        // null 체크 로직 짜렴
        if(sortKeyword == null) {
            sortKeyword = "1";
        }

        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("sortKeyword", sortKeyword);

        return myPageMapper.findUserScheduleById(param);
    }

    @Override
    public AttendanceDto getUserAttendance(int empId) {
        AttendanceDto attendance = myPageMapper.findAttendanceById(empId);
        return attendance;
    }

    public void insertUserSchedule(UserScheduleDto schedule) {
        myPageMapper.insertUserSchedule(schedule);

    }

    @Override
    public void deleteTodoList(int scheduleNo) {
        myPageMapper.deleteUserScheduleColumnByScheduleNo(scheduleNo);
    }


    @Override
    public void editEmpEmail(int empId, String empEmail) {
        myPageMapper.updateEmpEmailByEmpId(empId, empEmail);
    }

    @Override
    public void editEmpInformationDetail(EmployeeDetailDto employeeDetail) {

        myPageMapper.updateEmployeeDetailByEmpId(employeeDetail);
    }


    @Override
    public int getUserAttemptsCount(int empId, String keyword, String startDate, String endDate) {
        Map<String, Object> param= new HashMap<>();
        param.put("empId", empId);
        param.put("keyword", keyword);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return loginAttemptMapper.findLoginAttemptsCountById(param);
    }

    @Override
    public int getUserAttendanceCount(int empId, String keyword, String startDate, String endDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("keyword", keyword);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return myPageMapper.findAttendanceCountById(param);
    }

    @Override
    public List<AttendanceDto> findAttendanceByEmpId(int empId, int start, String keyword, String startDate, String endDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("start", start);
        param.put("keyword", keyword);
        param.put("startDate", startDate);
        param.put("endDate", endDate);

        List<AttendanceDto> attendance = myPageMapper.selectAttendanceById(param);
        return attendance;
    }

    @Override
    public List<LoginAttemptDto> getUserLoginAttemptLog(int empId, int start, String keyword, String startDate, String endDate) {

        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("start", start);
        param.put("keyword", keyword);
        param.put("startDate", startDate);
        param.put("endDate", endDate);

        List<LoginAttemptDto> attempt = myPageMapper.findLoginAttemptsById(param);
        return attempt;
    }

    @Override
    public void saveUserFile(UserFileBoxDto userFile) {
        myPageMapper.insertUserFile(userFile);
    }

    @Override
    public List<UserFileBoxDto> getUserFileList(int empId, int start, String keyword) {
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("start", start);
        param.put("keyword", keyword);
        List<UserFileBoxDto> userFileList = myPageMapper.findAllUserFileBoxByEmpId(param);
        return userFileList;
    }

    @Override
    public UserFileBoxDto getUserFileByFileNo(int fileNo) {
        UserFileBoxDto attach = myPageMapper.findUserFileByFileNo(fileNo);
        return attach;
    }

    @Override
    public int getUserFileCount(int empId, String keyword) {
        return myPageMapper.findUserFileBoxCountByEmpId(empId, keyword);
    }

    @Override
    public void deleteUserFile(Integer fileNo) {
        myPageMapper.deleteUserFileByFileNo(fileNo);
    }

    @Override
    public Integer getAnnualCount(int empId) {

        return hrMapper.findAuunalCountByEmpId(empId);
    }

    @Override
    public List<EmployeeDto> findAllEmployee() {
        return hrMapper.findAllEmployee();
    }

    @Override
    public List<UserBookmarkDto> getUserBookmarkByEmpId(int empId, int start, String option, String keyword, String sortName, String sortValue){
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("start", start);
        param.put("option", option);
        param.put("keyword", keyword);
        param.put("sortName", sortName);
        param.put("sortValue", sortValue);

        return myPageMapper.findUserBookmarkByEmpId(param);
    }

    @Override
    public void UserBookmarkAdd(int empId, int contactId) {
        myPageMapper.insertUserBookmarkByEmpIdAndContactId(empId, contactId);
    }

    @Override
    public void deleteUserBookmark(int empId, List<UserBookmarkDto> bookmarks) {

        for(UserBookmarkDto bookmark : bookmarks) {
            int contactId = Integer.parseInt(String.valueOf(bookmark.getContactId())); // 즐찾 당한사람
            myPageMapper.deleteUserBookmarkByEmpIdAndContactId(empId, contactId);
        }
    }

    @Override
    public int getUserBookmarkCount(int empId, String option, String keyword) {
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("option", option);
        param.put("keyword", keyword);
        return myPageMapper.findUserBookmarkCount(param);
    }

    @Override
    public void updateUserBookmarkSet(int empId, int contactId, Boolean bookmarkActive) {

        myPageMapper.updateUserBookmarkByEmpIdAndContactId(empId, contactId, bookmarkActive);

    }

    @Override
    public List<String> getAttendanceStatus(){
        return myPageMapper.findAllAttendanceStatus();
    }
}
