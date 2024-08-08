package com.groupware.controller;

import com.groupware.dto.*;
import com.groupware.service.ProjectService;
import com.groupware.ui.ProjectPager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(path = {"/schedule"})
public class ProjectController {

    @Setter(onMethod_ = {@Autowired})
    private ProjectService projectService;

    @PostMapping(path = {"/add-project-emp"})
    @ResponseBody
    public String addProjectEmp(int empId, int[] deptArr, @RequestParam(name = "empArr", required = false) int[] empArr) {
        String result = "failDept";

        EmployeeDto employee = projectService.findEmployee(empId);
        if (employee == null) {
            return "fail";
        }

        for (int deptNo : deptArr) {
            if (deptNo == employee.getDeptNo()) {
                result = "success";
                break;
            }
        }

        if (empArr != null) {
            for (int empNo : empArr) {
                if (empNo == employee.getEmpId()) {
                    result = "success";
                    break;
                } else {
                    result = "failEmp";
                }
            }
        }

        return result;
    }

    @GetMapping(path = {"/project-people"})
    public String projectPeople(int[] empArr, Model model) {

        List<EmployeeDto> employees = new ArrayList<>();
        for (int empId : empArr) {
            EmployeeDto employee = projectService.findEmployee(empId);
            employees.add(employee);
        }
        model.addAttribute("employees", employees);

        return "schedule/project-people";
    }

    @GetMapping(path = {"/project-pm"})
    public String projectPm(@RequestParam(name = "empId", required = false) Integer empId, Model model) {

        EmployeeDto employee = null;
        if (empId != null) {
            employee = projectService.findEmployee(empId);
        }
        model.addAttribute("employee", employee);

        return "schedule/project-pm";
    }

    @PostMapping(path = {"/add-project"})
    @ResponseBody
    public String addProject(ProjectDto project, String[] deptArr, String[] employeeArr) {

        ProjectDto lastProject = projectService.addProject(project);

        for (String dept : deptArr) {
            int deptNo = Integer.parseInt(dept);
            projectService.addProjectDept(lastProject.getScheduleNo(), deptNo);
        }

        for (String emp : employeeArr) {
            int empId = Integer.parseInt(emp);
            projectService.addProjectPeople(lastProject.getScheduleNo(), empId);
        }

        return "success";
    }

    @GetMapping(path = {"/list"})
    public String list(HttpSession session, Model model,
                       @RequestParam(name = "deptNo", required = false) Integer deptNo) {

        int parameterDeptNo = 0;

        if (deptNo == null) {
            EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
            parameterDeptNo = user.getDeptNo();
//            parameterDeptNo = 1;
        } else {
            parameterDeptNo = deptNo;
        }

        DepartmentDto dept = projectService.findDepartment(parameterDeptNo);
        List<DepartmentDto> deptes = projectService.findAllDeptes();

        model.addAttribute("dept", dept);
        model.addAttribute("deptes", deptes);

        return "schedule/list";
    }

    @GetMapping(path = {"/project-list"})
    public String projectList(HttpSession session, Model model,
                              HttpServletRequest req,
                              @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                              @RequestParam(name = "sortIdx", defaultValue = "0") int sortIdx,
                              @RequestParam(name = "searchValue", defaultValue = "") String searchValue,
                              @RequestParam(name = "projectStatus", defaultValue = "진행중") String projectStatus,
                              @RequestParam(name = "deptNo", required = false) Integer deptNo) throws ParseException {

        List<ProjectDto> projects = new ArrayList<>();
        int deptNoParameter = 0;
        if (deptNo == null) {
            EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
            deptNoParameter = user.getDeptNo();
        } else {
            deptNoParameter = deptNo;
        }

        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 5;
        int dataCount = projectService.getProjectsCount(deptNoParameter, searchValue, projectStatus);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);

        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        projects = projectService.findProjects(deptNoParameter, sortIdx, searchValue, projectStatus, start);

        // D-Day 계산
        ArrayList<Integer> dDay = new ArrayList<>();
        String todayFm = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())); // 오늘날짜
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (ProjectDto project : projects) {
            Date endDate = project.getScheduleEndDate();
            Date todayDate = new Date(dateFormat.parse(todayFm).getTime());
            long calculate = endDate.getTime() - todayDate.getTime();

            int dDays = (int) (calculate / (24 * 60 * 60 * 1000));
            dDay.add(dDays);
        }

        model.addAttribute("dDay", dDay);
        model.addAttribute("projects", projects);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);


        return "schedule/project-list";
    }

    @GetMapping(path = {"/edit-project"})
    public String editProject(int projectNo, Model model) {

        ProjectDto project = projectService.findProjectByScheduleNo(projectNo);
        List<DepartmentDto> depts = projectService.findDepartmentAll();
        model.addAttribute("depts", depts);
        model.addAttribute("project", project);

        return "schedule/edit-project";
    }

    @PostMapping(path = {"/edit-project"})
    @ResponseBody
    public String editProjectPost(ProjectDto project, String[] deptArr, String[] employeeArr) {

        projectService.editProject(project);

        // data 삭제
        projectService.removeProjectDept(project.getScheduleNo());
        projectService.removeProjectPeople(project.getScheduleNo());

        // projectNo를 select Count를 해서 그 숫자만큼 참여부서,인원 데이터 삭제
        // 그다음 다시 insert
        for (String dept : deptArr) {
            int deptNo = Integer.parseInt(dept);
            projectService.addProjectDept(project.getScheduleNo(), deptNo);
        }

        for (String emp : employeeArr) {
            int empId = Integer.parseInt(emp);
            projectService.addProjectPeople(project.getScheduleNo(), empId);
        }

        return "success";
    }

    @GetMapping(path = {"/update-list"})
    @ResponseBody
    public String updateList() {

        return "success";
    }

    @GetMapping(path = {"/project-calendar"})
    public String projectCalendar(@RequestParam(name = "deptNo", required = false) Integer deptNo,
                                  Model model, HttpSession session) {
        int deptNum = 0;
        if (deptNo == null) {
            EmployeeDto employee = (EmployeeDto) session.getAttribute("loginUser");
            deptNum = employee.getDeptNo();
        } else {
            deptNum = deptNo;
        }
        List<ProjectDto> projects = projectService.findAllProjectsByDeptNo(deptNum);
        List<DepartmentDto> depts = projectService.findAllDeptes();

        model.addAttribute("projects", projects);
        model.addAttribute("depts", depts);

        return "schedule/project-calendar";
    }

    @PostMapping(path = {"/modify-project-status"})
    @ResponseBody
    public String modifyProjectStatus(int projectNo, String status) {
        projectService.modifyProjectStatus(projectNo, status.trim());

        return "success";
    }

    @PostMapping(path = {"/remove-project-status"})
    @ResponseBody
    public String removeProjectStatus(int projectNo) {
        projectService.removeProject(projectNo);

        return "success";
    }

    // Project-Detail
    @GetMapping(path = {"/project-detail"})
    public String detail(int projectNo, Model model) {

        ProjectDto project = projectService.findProjectByScheduleNo(projectNo);

        model.addAttribute("project", project);

        return "schedule/project-detail";
    }

    @GetMapping(path = {"/project-memo"})
    public String projectMemo(int category, int projectNo, Model model) {

        List<ProjectBoardDto> notes = projectService.findBoardsByScheduleNo(0, category, projectNo);
        model.addAttribute("notes", notes);

        return "schedule/project-memo";
    }

    @PostMapping(path = {"/write-memo"})
    @ResponseBody
    public String writeMemo(ProjectBoardDto board) {

        String result = "success";
        if (board.getScheduleBoardContent().length() == 0) {
            result = "fail";
        } else {
            projectService.writeMemo(board);
        }

        return result;

    }

    @PostMapping(path = {"/modify-memo"})
    @ResponseBody
    public String modifyMemo(ProjectBoardDto board) {

        String result = "success";
        if (board.getScheduleBoardContent().length() == 0) {
            result = "fail";
        } else {
            projectService.modifyMemo(board);
        }

        return result;

    }

    @PostMapping(path = {"/delete-memo"})
    @ResponseBody
    public String deleteMemo(int scheduleBoardNo) {

        projectService.deleteMemo(scheduleBoardNo);

        return "success";
    }

    // 회의
    @GetMapping(path = {"/meeting"})
    public String meeting(int projectNo) {
        return "schedule/project-meeting";
    }

    @GetMapping(path = {"/meeting-list"})
    public String meetingList(int listCount, int category, int projectNo, Model model) {
        List<ProjectBoardDto> meetings = projectService.findBoardsByScheduleNo(listCount, category, projectNo);
        ProjectDto project = projectService.findProjectByScheduleNo(projectNo);
        model.addAttribute("meetings", meetings);
        model.addAttribute("project", project);

        return "schedule/meeting-list";
    }

    @PostMapping(path = {"/write-meeting"})
    public String writeMeeting(ProjectBoardDto board, @RequestParam(name = "scheduleNo") int projectNo) {

        projectService.writeMeeting(board);

        return "redirect:/schedule/project-detail?projectNo=" + projectNo;
    }

    @PostMapping(path = {"/modify-meeting"})
    @ResponseBody
    public String modifyMeeting(ProjectBoardDto board) {
        String result = "success";
        if (board.getScheduleBoardContent().length() == 0) {
            result = "fail";
        } else {
            projectService.modifyMemo(board);
        }

        return result;
    }

    @PostMapping(path = {"/delete-meeting"})
    @ResponseBody
    public String deleteMeeting(int scheduleBoardNo) {
        projectService.deleteMeeting(scheduleBoardNo);

        return "success";
    }

    @PostMapping(path = {"/fix-meeting"})
    @ResponseBody
    public String fixMeeting(int scheduleBoardNo) {
        projectService.fixMeeting(scheduleBoardNo);

        return "success";
    }

    @PostMapping(path = {"/add-emp"})
    @ResponseBody
    public String addEmp(int scheduleNo, int empId) {
        String result = "success";
        EmployeeDto employee = projectService.findEmployeeByEmpIdANDScheduleNo(scheduleNo, empId);
        if (employee == null) {
            projectService.addEmp(scheduleNo, empId);
        } else {
            result = "fail";
        }

        return result;
    }

    @GetMapping(path = {"/remove-emp"})
    public String removeEmp(int empId, int projectNo) {
        projectService.removeEmployee(empId, projectNo);

        return "redirect:/schedule/project-detail?projectNo=" + projectNo;
    }

    // Todo
    @GetMapping(path = {"/todo"})
    public String todo(int projectNo, int empId, Model model) {
        ProjectDto project = projectService.findProject(projectNo);
        EmployeeDto employee = projectService.findEmployee(empId);

        model.addAttribute("project", project);
        model.addAttribute("employee", employee);

        return "schedule/todo";
    }

//    public static void main(String[] args) {
//        // Example Timestamp
//        Timestamp userScheduleStartDate = Timestamp.valueOf("2024-07-30 10:00:00");
//        System.out.println("Original Timestamp: " + userScheduleStartDate);
//
//        // Subtract 9 hours
//        Timestamp adjustedTimestamp = subtractHours(userScheduleStartDate, 9);
//        System.out.println("Adjusted Timestamp: " + adjustedTimestamp);
//    }
//
//    public static Timestamp subtractHours(Timestamp timestamp, int hours) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(timestamp);
//        calendar.add(Calendar.HOUR_OF_DAY, -hours);
//        return new Timestamp(calendar.getTimeInMillis());
//    }

    @PostMapping(path = {"/write-todo"})
    @ResponseBody
    public String writeTodo(@RequestBody ProjectUserScheduleDto schedule) {

        Timestamp adjustedStartDate = subtractHours(schedule.getUserScheduleStartDate(), 9);
        Timestamp adjustedEndDate = subtractHours(schedule.getUserScheduleEndDate(), 9);

        schedule.setUserScheduleStartDate(adjustedStartDate);
        schedule.setUserScheduleEndDate(adjustedEndDate);

        projectService.writeTodo(schedule);

        return "success";
    }


    @GetMapping(path = {"/todo-list"})
    public String todoList(@RequestParam(name = "projectNo") int projectNo,
                           @RequestParam(name = "empId") int empId,
                           @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                           HttpServletRequest req,
                           Model model) {

        // 페이징 처리
        int pageSize = 5;
        int pagerSize = 3;
        int dataCount = projectService.getTodoCount(projectNo, empId);
        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1);

        ProjectPager pager = new ProjectPager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);
        List<ProjectUserScheduleDto> schedules = projectService.findProjectUserSchedules(projectNo, empId, start);

        model.addAttribute("schedules", schedules);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("dataCount", dataCount);


        return "schedule/todo-list";
    }

    @PostMapping(path = {"/modify-todo"})
    @ResponseBody
    public String modifyTodo(@RequestBody ProjectUserScheduleDto schedule) {

        Timestamp adjustedStartDate = subtractHours(schedule.getUserScheduleStartDate(), 9);
        Timestamp adjustedEndDate = subtractHours(schedule.getUserScheduleEndDate(), 9);

        schedule.setUserScheduleStartDate(adjustedStartDate);
        schedule.setUserScheduleEndDate(adjustedEndDate);

        projectService.updateTodo(schedule);

        return "success";
    }

    @PostMapping(path = {"/modify-status"})
    @ResponseBody
    public String modifyStatus(ProjectUserScheduleDto schedule) {

        projectService.updateStatus(schedule);

        return "success";
    }

    @PostMapping(path = {"/delete-todo"})
    @ResponseBody
    public String deleteTodo(int userScheduleNo) {
        projectService.deleteTodo(userScheduleNo);

        return "success";
    }

    // Project 추가
    @GetMapping(path = {"/write-project"})
    public String writeProject(Model model) {
        List<DepartmentDto> depts = projectService.findDepartmentAll();
        model.addAttribute("depts", depts);

        return "schedule/write-project";

    }

    public static Timestamp subtractHours(Timestamp timestamp, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.HOUR_OF_DAY, -hours);
        return new Timestamp(calendar.getTimeInMillis());
    }
}
