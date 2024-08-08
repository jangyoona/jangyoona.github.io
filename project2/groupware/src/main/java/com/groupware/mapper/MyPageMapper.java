package com.groupware.mapper;

import com.groupware.dto.*;
import com.groupware.loginAttempt.LoginAttemptDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MyPageMapper {
    List<UserScheduleDto> findUserScheduleById(Map param);
    AttendanceDto findAttendanceById(int empId);

    void insertUserSchedule(UserScheduleDto schedule);
    void deleteUserScheduleColumnByScheduleNo(int scheduleNo);
    void updateEmpEmailByEmpId(@RequestParam("empId") int empId, @RequestParam("empEmail") String empEmail);
    void updateEmployeeDetailByEmpId(EmployeeDetailDto employeeDetail);


    int findAttendanceCountById(Map param);

    List<AttendanceDto> selectAttendanceById(Map param);

    List<LoginAttemptDto> findLoginAttemptsById(Map param);

    void insertUserFile(UserFileBoxDto userFile);

    List<UserFileBoxDto> findAllUserFileBoxByEmpId(Map param);

    UserFileBoxDto findUserFileByFileNo(int fileNo);

    int findUserFileBoxCountByEmpId(@RequestParam("empId") int empId, @RequestParam("keyword") String keyword);

    void deleteUserFileByFileNo(Integer fileNo);

    List<UserBookmarkDto> findUserBookmarkByEmpId(Map param);

    void insertUserBookmarkByEmpIdAndContactId(@RequestParam("empId") int empId, @RequestParam("contactId") int contactId);

    void deleteUserBookmarkByEmpIdAndContactId(@RequestParam("empId") int empId, @RequestParam("contactId") int contactId);

    int findUserBookmarkCount(Map param);

    void updateUserBookmarkByEmpIdAndContactId(@RequestParam("empId") int empId, @RequestParam("contactId") int contactId, @RequestParam("bookmarkActive") Boolean bookmarkActive);

    List<String> findAllAttendanceStatus();
}
