package com.groupware.service;

import com.groupware.dto.*;
import com.groupware.loginAttempt.LoginAttemptDto;

import java.util.List;

public interface MyPageService {
    List<UserScheduleDto> getUserSchedule(int empId, String sortKeyword);
    AttendanceDto getUserAttendance(int empId);

    void deleteTodoList(int scheduleNo);

    void insertUserSchedule(UserScheduleDto schedule);

    void editEmpEmail(int empId, String empEmail);
    void editEmpInformationDetail(EmployeeDetailDto employeeDetail);

    List<LoginAttemptDto> getUserLoginAttemptLog(int empId, int pageNum, String keyword, String startDate, String endDate);

    int getUserAttemptsCount(int empId, String keyword, String startDate, String endDate);

    int getUserAttendanceCount(int empId, String keyword, String startDate, String endDate);

    List<AttendanceDto> findAttendanceByEmpId(int empId, int start, String keyword, String startDate, String endDate);

    void saveUserFile(UserFileBoxDto userFile);

    List<UserFileBoxDto> getUserFileList(int empId, int start, String keyword);

    UserFileBoxDto getUserFileByFileNo(int fileNo);

    int getUserFileCount(int empId, String keyword);

    void deleteUserFile(Integer fileNo);

    Integer getAnnualCount(int empId);


    List<EmployeeDto> findAllEmployee();

    List<UserBookmarkDto> getUserBookmarkByEmpId(int empId, int start, String option, String keyword, String sortName, String sortValue);

    void UserBookmarkAdd(int empId, int contactId);

    void deleteUserBookmark(int empId, List<UserBookmarkDto> bookmarks);

    int getUserBookmarkCount(int empId, String option, String keyword);

    void updateUserBookmarkSet(int empId, int contactId, Boolean bookmarkActive);

    List<String> getAttendanceStatus();
}
