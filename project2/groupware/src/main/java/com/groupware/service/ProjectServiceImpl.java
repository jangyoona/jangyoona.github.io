package com.groupware.service;

import com.groupware.dto.*;
import com.groupware.mapper.ProjectMapper;
import lombok.Setter;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectServiceImpl implements ProjectService{

    @Setter
    private ProjectMapper projectMapper;

    // 로그인된 사용자 부서 번호로 프로젝트들 찾기 AND 정렬
    @Override
    public List<ProjectDto> findProjects(int deptNo, int sortIdx, String searchValue, String projectStatus, int start) {

        List<ProjectDto> projects = new ArrayList<>();

        if (!searchValue.isEmpty()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("searchValue", searchValue);
            map.put("projectStatus", projectStatus);
            map.put("deptNo", deptNo);
            map.put("start", start);
            if (sortIdx == 0) {
                projects = projectMapper.selectProjectByProjectName(map);
            } else if (sortIdx == 1) {
                projects = projectMapper.selectProjectByProjectNameANDImportance(map);
            } else if (sortIdx == 2) {
                projects = projectMapper.selectProjectByProjectNameANDEndDate(map);
            }
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("deptNo", deptNo);
            map.put("projectStatus", projectStatus);
            map.put("start", start);
            if (sortIdx == 0) {
                projects = projectMapper.selectProjectByDept(map);
            } else if (sortIdx == 1) {
                projects = projectMapper.selectProjectByImportance(map);
            } else if (sortIdx == 2) {
                projects = projectMapper.selectProjectByEndDate(map);
            }

        }


        for (ProjectDto project : projects) {
            List<DepartmentDto> depts = projectMapper.selectDeptsByScheduleNo(project.getScheduleNo());
            project.setDepts(depts);
            int avg = 0;
            for (DepartmentDto dept : depts) {
                int complete = projectMapper.selectDeptComplete(project.getScheduleNo(), dept.getDeptNo());
                int allTodo = projectMapper.selectDeptTodoCount(project.getScheduleNo(), dept.getDeptNo());
                int percent = 0;
                if (allTodo != 0) {
                    double divide = (double)complete / allTodo;
                    percent = (int)Math.round(divide * 100);
                }
                dept.setPercent(percent);
                avg += percent;
            }
            int deptSize = depts.size() * 100;
            double allDivide = (double)avg / deptSize;
            avg  = (int)Math.round(allDivide * 100);
            project.setPercent(avg);

            List<EmployeeDto> employees = projectMapper.selectEmployeesByScheduleNo(project.getScheduleNo());
            project.setEmployees(employees);
        }

        return projects;
    }

    // 사용자가 선택한 조건에 맞는 프로젝트 개수
    @Override
    public int getProjectsCount(int deptNoParameter, String searchValue, String projectStatus) {

        int projectsCount = 0;

        if (!searchValue.isEmpty()) {
            projectsCount = projectMapper.selectProjectsCountByProjectName(searchValue);
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("deptNo", deptNoParameter);
            map.put("projectStatus", projectStatus);
            projectsCount = projectMapper.selectProjectsCountByDeptNoANDProjectStatus(map);

        }

        return projectsCount;
    }

    // 선택한 부서 이름 찾기
    @Override
    public DepartmentDto findDepartment(int deptNo) {
        return projectMapper.selectDepartment(deptNo);
    }

    @Override
    public List<DepartmentDto> findAllDeptes() {
        List<DepartmentDto> deptes = projectMapper.selectAllDeptes();
        return deptes;
    }

    @Override
    public List<ProjectDto> findProjectsByDeptNo(int deptNo) {
        List<ProjectDto> projects = projectMapper.selectProjectsByDeptNo(deptNo);
        return projects;
    }


    // Project-Detail
    @Override
    public ProjectDto findProjectByScheduleNo(int projectNo) {
        ProjectDto project = projectMapper.selectProjectByScheduleNo(projectNo);

        List<DepartmentDto> depts = projectMapper.selectDeptsByScheduleNo(project.getScheduleNo());
        for (DepartmentDto dept : depts) {
            int complete = projectMapper.selectDeptComplete(projectNo, dept.getDeptNo());
            int allTodo = projectMapper.selectDeptTodoCount(projectNo, dept.getDeptNo());
            int percent = 0;
            if (allTodo != 0) {
                double divide = (double)complete / allTodo;
                percent = (int)Math.round(divide * 100);
            }
            dept.setPercent(percent);
        }
        project.setDepts(depts);

        // 해당 프로젝트의 협업하는 부서의 참여인원으로 조회해야함 (수정해야됨)
        List<EmployeeDto> employees = projectMapper.selectEmployeesByScheduleNo(project.getScheduleNo());
        for (EmployeeDto employee : employees) {
            int complete = projectMapper.selectCompleteStatus(projectNo, employee.getEmpId());
            int allTodo = projectMapper.selectTodoCountByEmpId(projectNo, employee.getEmpId());
            int percent = 0;
            if (allTodo != 0) {
                double divide = (double)complete / allTodo;
                percent = (int)Math.round(divide * 100);
            }
            employee.setPercent(percent);
        }
        project.setEmployees(employees);

        return project;
    }

    @Override
    public void writeMemo(ProjectBoardDto board) {
        projectMapper.insertMemo(board);
    }

    @Override
    public List<ProjectBoardDto> findBoardsByScheduleNo(int listCount, int category, int projectNo) {

        List<ProjectBoardDto> boards = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("category", category);
        map.put("projectNo", projectNo);

        if (category == 1) {
            boards = projectMapper.selectBoardsByScheduleNo(map);
        } else {
            listCount = listCount + 5;
            map.put("listCount", listCount);
            boards = projectMapper.selectMeetingsByScheduleNo(map);
        }

        for (ProjectBoardDto note : boards) {
            EmployeeDto employee = projectMapper.selectEmployeeByEmpId(note.getEmpId());
            DepartmentDto dept = projectMapper.selectDepartment(employee.getDeptNo());
            note.setDeptName(dept.getDeptName());
            note.setEmployee(employee);
        }

        return boards;
    }

    @Override
    public void modifyMemo(ProjectBoardDto board) {
        projectMapper.updateMemo(board);
    }

    @Override
    public void deleteMemo(int scheduleBoardNo) {
        projectMapper.deleteMemo(scheduleBoardNo);
    }

    @Override
    public void writeMeeting(ProjectBoardDto board) {
        projectMapper.insertMeeting(board);
    }

    @Override
    public void deleteMeeting(int scheduleBoardNo) {
        projectMapper.updateMeetingActive(scheduleBoardNo);
    }

    @Override
    public void fixMeeting(int scheduleBoardNo) {
        projectMapper.updateMeetingActiveTrue(scheduleBoardNo);
    }

    @Override
    public void writeTodo(ProjectUserScheduleDto schedule) {
        projectMapper.insertTodo(schedule);
    }

    @Override
    public List<ProjectUserScheduleDto> findProjectUserSchedules(int projectNo, int empId, int pageNo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("projectNo", projectNo);
        map.put("empId", empId);
        List<ProjectUserScheduleDto> schedules = projectMapper.selectProjectUserSchedules2(projectNo, empId, pageNo);

        return schedules;
    }

    @Override
    public EmployeeDto findEmployee(int empId) {
        EmployeeDto employee = projectMapper.selectEmployeeByEmpId(empId);
        return employee;
    }

    @Override
    public ProjectDto findProject(int projectNo) {
        ProjectDto project = projectMapper.selectProject(projectNo);
        return project;
    }

    @Override
    public void updateTodo(ProjectUserScheduleDto schedule) {
        projectMapper.updateTodo(schedule);
    }

    @Override
    public void updateStatus(ProjectUserScheduleDto schedule) {
        projectMapper.updateStatus(schedule);
    }

    @Override
    public void deleteTodo(int userScheduleNo) {
        projectMapper.deleteTodo(userScheduleNo);
    }

    @Override
    public int getTodoCount(int projectNo, int empId) {
        int count = projectMapper.selectTodoAll(projectNo, empId);
        return count;
    }

    @Override
    public EmployeeDto findEmployeeByEmpIdANDScheduleNo(int scheduleNo, int empId) {
        EmployeeDto employee = projectMapper.selectEmployeeByEmpIdANDScheduleNo(scheduleNo, empId);
        return employee;
    }

    @Override
    public void addEmp(int scheduleNo, int empId) {
        EmployeeDto employee = projectMapper.selectEmployeeByEmpId(empId);
        DepartmentDto dept = projectMapper.selectProjectDept(scheduleNo, employee.getDeptNo());
        if (dept == null) {
            projectMapper.insertDept(employee.getDeptNo(), scheduleNo);
        }
        projectMapper.insertEmployee(scheduleNo, empId);
    }

    @Override
    public void removeEmployee(int empId, int projectNo) {
        projectMapper.deleteEmployee(empId, projectNo);
        projectMapper.deleteUserScheduleEmployee(empId, projectNo);
    }

    @Override
    public List<DepartmentDto> findDepartmentAll() {
        List<DepartmentDto> depts = projectMapper.selectAllDeptes();
        return depts;
    }

    @Override
    public ProjectDto addProject(ProjectDto project) {
        projectMapper.insertProject(project);
        ProjectDto lastProject = projectMapper.selectLastProject();

        return lastProject;
    }

    @Override
    public void addProjectDept(int scheduleNo, int deptNo) {
        projectMapper.insertProjectDept(scheduleNo, deptNo);
    }

    @Override
    public void addProjectPeople(int scheduleNo, int empId) {
        projectMapper.insertProjectPeople(scheduleNo, empId);
    }

    @Override
    public void modifyProjectStatus(int projectNo, String status) {
        projectMapper.updateProjectStatus(projectNo, status);
    }

    @Override
    public void removeProject(int projectNo) {
        projectMapper.deleteProject(projectNo);
    }

    @Override
    public List<ProjectDto> findAllProjects() {
        List<ProjectDto> projects = projectMapper.selectAllProjects();
        return projects;
    }

    @Override
    public void editProject(ProjectDto project) {
        projectMapper.updateProject(project);
    }

    @Override
    public void removeProjectDept(int scheduleNo) {
        projectMapper.deleteProjectDept(scheduleNo);
    }

    @Override
    public void removeProjectPeople(int scheduleNo) {
        projectMapper.deleteProjectPeople(scheduleNo);
    }

    @Override
    public List<ProjectDto> findAllProjectsByDeptNo(int deptNo) {
        return projectMapper.selectAppProjectsByDeptNo(deptNo);
    }

    @Override
    public List<ProjectUserScheduleDto> findProjectUserSchedulesByEmpId(int empId) {

        List<ProjectUserScheduleDto> userSchedules = projectMapper.selectProjectUserSchedulesByEmpId(empId);
        for (ProjectUserScheduleDto userSchedule : userSchedules) {
            ProjectDto project = projectMapper.selectProject(userSchedule.getScheduleNo());
            userSchedule.setProject(project);
        }

        return userSchedules;
    }


}
