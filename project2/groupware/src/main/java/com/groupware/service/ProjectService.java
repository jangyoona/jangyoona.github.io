package com.groupware.service;

import com.groupware.dto.*;

import java.util.List;

public interface ProjectService {

    List<ProjectDto> findProjects(int deptNo, int sortIdx, String searchValue, String projectStatus, int start);

    DepartmentDto findDepartment(int deptNo);

    List<DepartmentDto> findAllDeptes();

    List<ProjectDto> findProjectsByDeptNo(int deptNo);

    int getProjectsCount(int deptNoParameter, String searchValue, String projectStatus);

    ProjectDto findProjectByScheduleNo(int projectNo);

    void writeMemo(ProjectBoardDto board);

    List<ProjectBoardDto> findBoardsByScheduleNo(int listCount, int category, int projectNo);

    void modifyMemo(ProjectBoardDto board);

    void deleteMemo(int scheduleBoardNo);

    void writeMeeting(ProjectBoardDto board);

    void deleteMeeting(int scheduleBoardNo);

    void fixMeeting(int scheduleBoardNo);

    void writeTodo(ProjectUserScheduleDto schedule);

    List<ProjectUserScheduleDto> findProjectUserSchedules(int projectNo, int empId, int pageNo);

    EmployeeDto findEmployee(int empId);

    ProjectDto findProject(int projectNo);

    void updateTodo(ProjectUserScheduleDto schedule);

    void updateStatus(ProjectUserScheduleDto schedule);

    void deleteTodo(int userScheduleNo);

    int getTodoCount(int projectNo, int empId);

    EmployeeDto findEmployeeByEmpIdANDScheduleNo(int scheduleNo, int empId);

    void addEmp(int scheduleNo, int empId);

    void removeEmployee(int empId, int projectNo);

    List<DepartmentDto> findDepartmentAll();

    ProjectDto addProject(ProjectDto project);

    void addProjectDept(int scheduleNo, int deptNo);

    void addProjectPeople(int scheduleNo, int empId);

    void modifyProjectStatus(int projectNo, String status);

    void removeProject(int projectNo);

    List<ProjectDto> findAllProjects();

    void editProject(ProjectDto project);

    void removeProjectDept(int scheduleNo);

    void removeProjectPeople(int scheduleNo);

    List<ProjectDto> findAllProjectsByDeptNo(int deptNo);

    List<ProjectUserScheduleDto> findProjectUserSchedulesByEmpId(int empId);
}
