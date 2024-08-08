package com.groupware.mapper;

import com.groupware.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface ProjectMapper {
//    List<ProjectDto> selectProjectByDept(int deptNo, String projectStatus);
    List<ProjectDto> selectProjectByDept(HashMap<String, Object> map);

    List<DepartmentDto> selectDeptsByScheduleNo(int scheduleNo);

    List<EmployeeDto> selectEmployeesByScheduleNo(int scheduleNo);

    List<ProjectDto> selectProjectByImportance(HashMap<String, Object> map);

    DepartmentDto selectDepartment(int deptNo);

    List<ProjectDto> selectProjectByEndDate(HashMap<String, Object> map);

    List<ProjectDto> selectProjectByProjectName(HashMap<String, Object> map);

    List<DepartmentDto> selectAllDeptes();

    List<ProjectDto> selectProjectsByDeptNo(int deptNo);

    int selectProjectsCountByProjectName(String searchValue);

    int selectProjectsCountByDeptNoANDProjectStatus(HashMap<String, Object> map);

    List<ProjectDto> selectProjectByProjectNameANDImportance(HashMap<String, Object> map);

    List<ProjectDto> selectProjectByProjectNameANDEndDate(HashMap<String, Object> map);

    ProjectDto selectProjectByScheduleNo(int projectNo);

    void insertMemo(ProjectBoardDto board);

    List<ProjectBoardDto> selectBoardsByScheduleNo(HashMap<String, Object> map);

    EmployeeDto selectEmployeeByEmpId(int empId);

    void updateMemo(ProjectBoardDto board);

    void deleteMemo(int scheduleBoardNo);

    void insertMeeting(ProjectBoardDto board);

    List<ProjectBoardDto> selectMeetingsByScheduleNo(HashMap<String, Object> map);

    void updateMeetingActive(int scheduleBoardNo);

    void updateMeetingActiveTrue(int scheduleBoardNo);

    void insertTodo(ProjectUserScheduleDto schedule);

    List<ProjectUserScheduleDto> selectProjectUserSchedules(HashMap<String, Object> map);
    List<ProjectUserScheduleDto> selectProjectUserSchedules2(@Param("projectNo") int projectNo, @Param("empId")int empId, @Param("pageNo") int pageNo);

    ProjectDto selectProject(int scheduleNo);

    void updateTodo(ProjectUserScheduleDto schedule);

    void updateStatus(ProjectUserScheduleDto schedule);

    void deleteTodo(int userScheduleNo);

    int selectTodoAll(@Param("projectNo") int projectNo, @Param("empId") int empId);

    int selectCompleteStatus(@Param("projectNo") int projectNo, @Param("empId") int empId);

    int selectTodoCountByEmpId(@Param("projectNo") int projectNo, @Param("empId") int empId);

    int selectDeptComplete(@Param("projectNo") int projectNo, @Param("deptNo") int deptNo);

    int selectDeptTodoCount(@Param("projectNo") int projectNo, @Param("deptNo") int deptNo);

    EmployeeDto selectEmployeeByEmpIdANDScheduleNo(@Param("scheduleNo") int scheduleNo, @Param("empId") int empId);

    void insertEmployee(@Param("scheduleNo") int scheduleNo, @Param("empId") int empId);

    DepartmentDto selectProjectDept(@Param("scheduleNo") int scheduleNo, @Param("deptNo") int deptNo);

    void insertDept(@Param("deptNo") int deptNo, @Param("scheduleNo") int scheduleNo);

    void deleteEmployee(@Param("empId") int empId, @Param("projectNo") int projectNo);

    void deleteUserScheduleEmployee(@Param("empId") int empId, @Param("projectNo") int projectNo);

    void insertProject(ProjectDto project);

    ProjectDto selectLastProject();

    void insertProjectDept(@Param("scheduleNo") int scheduleNo, @Param("deptNo") int deptNo);

    void insertProjectPeople(@Param("scheduleNo") int scheduleNo, @Param("empId") int empId);

    void updateProjectStatus(@Param("projectNo") int projectNo, @Param("status") String status);

    void deleteProject(int projectNo);

    List<ProjectDto> selectAllProjects();

    void updateProject(ProjectDto project);

    void deleteProjectDept(int scheduleNo);

    void deleteProjectPeople(int scheduleNo);

    List<ProjectDto> selectAppProjectsByDeptNo(int deptNo);

    List<ProjectUserScheduleDto> selectProjectUserSchedulesByEmpId(int empId);
}
